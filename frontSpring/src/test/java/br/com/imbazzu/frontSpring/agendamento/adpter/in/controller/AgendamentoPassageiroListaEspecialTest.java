package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.CarregadorOpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MensagemErroApi;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MontadorAgendaDia;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MontadorDetalhesPassageiros;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.RespostaFlash;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.OpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;
import br.com.imbazzu.frontSpring.motorista.MotoristaApi;
import br.com.imbazzu.frontSpring.veiculo.core.FinalidadeAberturaVeiculo;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;

class AgendamentoPassageiroListaEspecialTest {
    @Test
    void modalDeCarroComumFixaListaEncaixeERecusaListaDiferente() {
        var api = mock(AgendamentoApi.class);
        var carregador = mock(CarregadorOpcoesAgendamento.class);
        var data = LocalDate.of(2026, 12, 1);
        var veiculo = new VeiculoAbertoResponseDto(UUID.randomUUID(), data, UUID.randomUUID(),
                "Carro", 4, null, null, 0, List.of());
        var encaixe = new ListaResponseDto(UUID.randomUUID(), "Encaixe", "", 0, 0, false,
                List.of(), List.of(), List.of(), List.of(), "ENCAIXE");
        when(api.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(veiculo), List.of()));
        when(carregador.carregarParaUsuario()).thenReturn(
                new OpcoesAgendamento(List.of(encaixe), List.of(), List.of(), List.of()));
        var pagina = new AgendamentoPaginaController(api, mock(PessoaApplicationPorta.class), carregador,
                mock(MontadorAgendaDia.class), mock(MontadorDetalhesPassageiros.class),
                mock(MensagemErroApi.class), mock(MotoristaApi.class));
        var model = new ExtendedModelMap();

        pagina.modalAgendamentoCarro(veiculo.id(), data, model);
        assertEquals(encaixe.id(), model.getAttribute("idListaSelecionada"));
        assertEquals(true, model.getAttribute("listaFixaInsercao"));

        var passageiro = new AgendamentoPassageiroController(api, mock(PessoaApplicationPorta.class),
                mock(VeiculoApplicationPorta.class), carregador,
                mock(MontadorAgendaDia.class), mock(RespostaFlash.class), mock(MensagemErroApi.class));
        assertThrows(ResponseStatusException.class, () -> passageiro.verificarVeiculos(
                data, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalTime.NOON,
                0, false, false, veiculo.id(), true, null,
                new ExtendedModelMap(), new RedirectAttributesModelMap()));
        verify(api, never()).verificarVeiculos(any(), any());
    }

    @Test
    void modalDeEmergenciaMantemListaDaAbertura() {
        var api = mock(AgendamentoApi.class);
        var carregador = mock(CarregadorOpcoesAgendamento.class);
        var data = LocalDate.of(2026, 12, 1);
        var lista = new ListaResponseDto(UUID.randomUUID(), "Emergência", "", 0, 0, false,
                List.of(), List.of(), List.of(), List.of(), "EMERGENCIA");
        var veiculo = new VeiculoAbertoResponseDto(UUID.randomUUID(), data, UUID.randomUUID(),
                "Carro", 4, null, LocalTime.of(8, 0), 0, List.of(), null,
                FinalidadeAberturaVeiculo.EMERGENCIA, lista.id());
        when(api.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(veiculo), List.of()));
        when(carregador.carregarParaUsuario()).thenReturn(
                new OpcoesAgendamento(List.of(lista), List.of(), List.of(), List.of()));
        var pagina = new AgendamentoPaginaController(api, mock(PessoaApplicationPorta.class), carregador,
                mock(MontadorAgendaDia.class), mock(MontadorDetalhesPassageiros.class),
                mock(MensagemErroApi.class), mock(MotoristaApi.class));
        var model = new ExtendedModelMap();

        pagina.modalAgendamentoCarro(veiculo.id(), data, model);
        assertEquals(lista.id(), model.getAttribute("idListaSelecionada"));
        assertEquals(true, model.getAttribute("informarHorarioSaida"));
    }

    @Test
    void paginaDeInsercaoEmOnibusFixaEncaixe() {
        var api = mock(AgendamentoApi.class);
        var carregador = mock(CarregadorOpcoesAgendamento.class);
        var detalhes = mock(MontadorDetalhesPassageiros.class);
        var motoristas = mock(MotoristaApi.class);
        var data = LocalDate.of(2026, 12, 1);
        var veiculo = new VeiculoAbertoResponseDto(UUID.randomUUID(), data, UUID.randomUUID(),
                "Ônibus", 40, null, LocalTime.of(8, 0), 0, List.of());
        var encaixe = new ListaResponseDto(UUID.randomUUID(), "Encaixe", "", 0, 0, false,
                List.of(), List.of(), List.of(), List.of(), "ENCAIXE");
        var opcoes = new OpcoesAgendamento(List.of(encaixe), List.of(), List.of(), List.of());
        when(api.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(veiculo), List.of()));
        when(carregador.carregarParaUsuario()).thenReturn(opcoes);
        when(carregador.incluirDestinosHistoricos(any(), any())).thenReturn(opcoes);
        when(carregador.incluirDestinosHistoricosCancelados(any(), any())).thenReturn(opcoes);
        when(detalhes.montarPorAgendamento(any(), any())).thenReturn(java.util.Map.of());
        when(motoristas.atribuicoes(data)).thenReturn(List.of());
        var pagina = new AgendamentoPaginaController(api, mock(PessoaApplicationPorta.class), carregador,
                mock(MontadorAgendaDia.class), detalhes, mock(MensagemErroApi.class), motoristas);
        var model = new ExtendedModelMap();

        pagina.inicio(data, veiculo.id(), false, true, null, null, false, model);
        assertEquals(encaixe.id(), model.getAttribute("idListaSelecionada"));
        assertEquals(true, model.getAttribute("listaFixaInsercao"));
    }

    @Test
    void naoVerificaNemCriaComListaDiferenteDoCarroEspecial() {
        var api = mock(AgendamentoApi.class);
        var data = LocalDate.of(2026, 12, 1);
        var veiculo = new VeiculoAbertoResponseDto(UUID.randomUUID(), data, UUID.randomUUID(),
                "Carro", 4, null, LocalTime.of(8, 0), 0, List.of(), null,
                FinalidadeAberturaVeiculo.EMERGENCIA, UUID.randomUUID());
        when(api.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(veiculo), List.of()));
        var controller = new AgendamentoPassageiroController(api, mock(PessoaApplicationPorta.class),
                mock(VeiculoApplicationPorta.class), mock(CarregadorOpcoesAgendamento.class),
                mock(MontadorAgendaDia.class), mock(RespostaFlash.class), mock(MensagemErroApi.class));
        var pessoa = UUID.randomUUID();
        var listaIncorreta = UUID.randomUUID();
        var destino = UUID.randomUUID();
        var hora = LocalTime.of(8, 0);

        assertThrows(ResponseStatusException.class, () -> controller.verificarVeiculos(
                data, pessoa, listaIncorreta, destino, hora, 0, false, true, veiculo.id(),
                true, null, new ExtendedModelMap(), new RedirectAttributesModelMap()));
        assertThrows(ResponseStatusException.class, () -> controller.criar(
                data, pessoa, listaIncorreta, UUID.randomUUID(), destino, hora, false, true,
                0, null, veiculo.id(), true, new RedirectAttributesModelMap()));
        verify(api, never()).verificarVeiculos(any(), any());
        verify(api, never()).criar(any(), any());
    }
}
