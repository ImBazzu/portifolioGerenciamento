package br.com.imbazzu.secretariaTransporte.lista.adpter.out.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes.RegraSeparacaoProjecao;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes.TipoListaDetalheProjecao;

class TipoListaConsultaMapperTest {

    @Test
    void incluiTipoEToleranciaEstruturadaNaRegraDeSeparacao() {
        var idLista = UUID.randomUUID();
        var idRegra = UUID.randomUUID();
        var idTipoVeiculo = UUID.randomUUID();
        var tipoVeiculo = mock(RegraSeparacaoProjecao.TipoVeiculoProjecao.class);
        var regra = mock(RegraSeparacaoProjecao.class);
        var lista = mock(TipoListaDetalheProjecao.class);
        when(tipoVeiculo.getId()).thenReturn(idTipoVeiculo);
        when(tipoVeiculo.getNome()).thenReturn("Onibus");
        when(regra.getId()).thenReturn(idRegra);
        when(regra.getTipoVeiculo()).thenReturn(tipoVeiculo);
        when(regra.getTempoToleranciaSegundos()).thenReturn(3_600L);
        when(regra.getTempoToleranciaNanos()).thenReturn(0);
        when(lista.getId()).thenReturn(idLista);
        when(lista.getNome()).thenReturn("Saude");
        when(lista.getRegrasCadastro()).thenReturn(List.of());
        when(lista.getRegrasSeparacao()).thenReturn(List.of(regra));

        var detalhe = TipoListaConsultaMapper.paraDetalhe(lista);
        var resultado = detalhe.regrasSeparacao().getFirst();

        assertEquals(idTipoVeiculo, resultado.idTipoVeiculo());
        assertEquals("Onibus", resultado.nome());
        assertEquals("01:00", resultado.tempoTolerancia());
        assertEquals(3_600, resultado.tempoToleranciaSegundos());
        assertEquals(0, resultado.tempoToleranciaNanos());
    }
}
