package br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import br.com.imbazzu.secretariaTransporte.lista.adpter.out.mapper.TipoListaConsultaMapper;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes.RegraCadastroProjecao;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes.TipoListaDetalheProjecao;
import br.com.imbazzu.secretariaTransporte.lista.application.query.buscar.ListaQueryBuscarHandler;
import br.com.imbazzu.secretariaTransporte.lista.core.porta.out.TipoListaPortaQuery;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;

class TipoListaDetalheTest {
    static Stream<Arguments> horarios() {
        return Stream.of(
                Arguments.of(List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), 3),
                Arguments.of(List.of(), 1));
    }

    @ParameterizedTest
    @MethodSource("horarios")
    void retornaDiasEIntervaloDasRegrasPersistidas(List<DayOfWeek> dias, int intervalo) {
        var id = UUID.randomUUID();
        var idVeiculo = UUID.randomUUID();
        var regra = mock(RegraCadastroProjecao.class);
        when(regra.getId()).thenReturn(UUID.randomUUID());
        when(regra.getIdsVeiculos()).thenReturn(List.of(idVeiculo));
        when(regra.getIdsCidades()).thenReturn(List.of());
        when(regra.getPrioridades()).thenReturn(List.of(UUID.randomUUID()));
        when(regra.getDiasSemana()).thenReturn(dias);
        when(regra.getIntervaloRepetacao()).thenReturn(intervalo);
        var projecao = mock(TipoListaDetalheProjecao.class);
        when(projecao.getId()).thenReturn(id);
        when(projecao.getNome()).thenReturn("Lista");
        when(projecao.getRegrasCadastro()).thenReturn(List.of(regra));
        when(projecao.getRegrasSeparacao()).thenReturn(List.of());

        var porta = mock(TipoListaPortaQuery.class);
        var detalhe = TipoListaConsultaMapper.paraDetalhe(projecao);
        when(porta.buscarPorId(id)).thenReturn(Optional.of(detalhe));
        var veiculos = mock(TipoVeiculoQueryPorta.class);
        when(veiculos.buscarAtivo(idVeiculo)).thenReturn(Optional.of(new TipoVeiculoOutput(idVeiculo, "Carro", 4)));
        var handler = new ListaQueryBuscarHandler(porta, mock(CidadeQueryPorta.class), veiculos);
        var controller = new TipoListaController(null, null, null, null, null, null, handler, null, null);

        var resposta = controller.buscar(id).getBody().regrasCadastro().getFirst();
        assertEquals(dias, resposta.diasSemana());
        assertEquals(intervalo, resposta.intervaloRepetacao());
    }
}
