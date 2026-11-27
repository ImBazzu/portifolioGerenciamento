package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.OpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeArquivadoResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.DestinoResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;
import br.com.imbazzu.frontSpring.usuario.UsuarioApi;

class CarregadorOpcoesAgendamentoTest {
    @Test
    void apresentaApenasListasLiberadasParaOUsuario() {
        var listas = mock(ListaApplicationPorta.class);
        var localidades = mock(LocalidadeApplicationPorta.class);
        var veiculos = mock(VeiculoApplicationPorta.class);
        var usuarios = mock(UsuarioApi.class);
        var permitida = new ListaResponseDto(UUID.randomUUID(), "Alta", "", 0, 0,
                false, List.of(), List.of(), List.of(), List.of());
        var bloqueada = new ListaResponseDto(UUID.randomUUID(), "Consulta", "", 0, 0,
                false, List.of(), List.of(), List.of(), List.of());
        when(listas.listarAtivas("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(permitida, bloqueada), 0, 10, 2, 1));
        when(listas.buscarAtiva(permitida.id())).thenReturn(permitida);
        when(listas.buscarAtiva(bloqueada.id())).thenReturn(bloqueada);
        when(localidades.listarCidades("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
        when(veiculos.listar("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
        when(usuarios.minhasListasAgendamento()).thenReturn(
                new UsuarioApi.VisibilidadeListas(true, List.of(permitida.id()), List.of()));

        assertEquals(List.of(permitida),
                new CarregadorOpcoesAgendamento(listas, veiculos, localidades, usuarios).carregarParaUsuario().listas());
    }

    @Test
    void selecionaSomenteOCarroPadraoDeQuatroLugares() {
        var carro = new TipoVeiculoResponseDto(UUID.randomUUID(), "Carro", 4);
        var opcoes = new OpcoesAgendamento(List.of(), List.of(
                new TipoVeiculoResponseDto(UUID.randomUUID(), "Carro especial", 4),
                new TipoVeiculoResponseDto(UUID.randomUUID(), "Carro", 5),
                carro), List.of(), List.of());

        assertEquals(carro, opcoes.carroQuatroLugares().orElseThrow());
        assertTrue(new OpcoesAgendamento(List.of(), List.of(), List.of(), List.of())
                .carroQuatroLugares().isEmpty());
    }

    @Test
    void encontraDestinoArquivadoEmCidadeArquivada() {
        var localidades = mock(LocalidadeApplicationPorta.class);
        var carregador = new CarregadorOpcoesAgendamento(mock(ListaApplicationPorta.class),
                mock(VeiculoApplicationPorta.class), localidades, mock(UsuarioApi.class));
        var cidadeId = UUID.randomUUID();
        var destinoId = UUID.randomUUID();
        var cidade = new CidadeArquivadoResponseDto(cidadeId, "Cidade histórica", "BA", "", 1, LocalDate.now());
        when(localidades.listarCidadesArquivadas("", 0))
                .thenReturn(new ResultadoPaginado<>(List.of(cidade), 0, 10, 1, 1));
        when(localidades.listarDestinos(cidadeId, "", 0))
                .thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
        when(localidades.listarDestinosArquivados(cidadeId, "", 0))
                .thenReturn(new ResultadoPaginado<>(List.of(), 0, 1, 1, 2));
        when(localidades.listarDestinosArquivados(cidadeId, "", 1))
                .thenReturn(new ResultadoPaginado<>(List.of(new DestinoResponseDto(destinoId, "Hospital antigo", 1)),
                        1, 1, 1, 2));
        var passageiro = new AgendamentoResponseDto(UUID.randomUUID(), LocalDate.now(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), destinoId, LocalTime.NOON, null,
                false, 0, null, List.of());
        var opcoes = new OpcoesAgendamento(List.of(), List.of(), List.of(), List.of());

        var destino = carregador.incluirDestinosHistoricos(opcoes, List.of(passageiro)).destino(destinoId).orElseThrow();

        assertEquals("Cidade histórica", destino.cidade());
        assertEquals("Hospital antigo", destino.nome());
    }
}
