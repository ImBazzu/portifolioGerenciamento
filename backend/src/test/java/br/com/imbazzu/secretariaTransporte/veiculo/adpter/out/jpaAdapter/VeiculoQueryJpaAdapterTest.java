package br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.jpaAdapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio.VeiculoQueryRepository;
import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio.projecoes.VeiculoHistoricoCidadeProjecao;
import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio.projecoes.VeiculoHistoricoResumoProjecao;

class VeiculoQueryJpaAdapterTest {

    @Test
    void buscaResumoEAgrupaCidadesDaMesmaData() {
        var repository = mock(VeiculoQueryRepository.class);
        var adapter = new VeiculoQueryJpaAdapter(repository);
        var idTipoVeiculo = UUID.randomUUID();
        var data = LocalDate.of(2026, 11, 5);
        var resumo = mock(VeiculoHistoricoResumoProjecao.class);
        when(resumo.getData()).thenReturn(data);
        when(resumo.getQtdVeiculo()).thenReturn(2L);
        when(resumo.getTotalPassageiros()).thenReturn(7L);
        when(repository.buscarResumo(idTipoVeiculo, PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(List.of(resumo), PageRequest.of(0, 20), 1));

        var cidadeB = cidade(data, "Belo Horizonte");
        var cidadeA = cidade(data, "Contagem");
        when(repository.buscarDestinosPorDatas(idTipoVeiculo, List.of(data)))
                .thenReturn(List.of(cidadeB, cidadeA));

        var resultado = adapter.buscarHistorico(idTipoVeiculo, 0, 20);

        assertEquals(1, resultado.totalElementos());
        assertEquals(idTipoVeiculo, resultado.conteudo().getFirst().id());
        assertEquals(data, resultado.conteudo().getFirst().data());
        assertEquals(2, resultado.conteudo().getFirst().quantidadeVeiculos());
        assertEquals("Belo Horizonte, Contagem", resultado.conteudo().getFirst().destinos());
        assertEquals(7, resultado.conteudo().getFirst().totalPassageiro());
        verify(repository).buscarDestinosPorDatas(idTipoVeiculo, List.of(data));
    }

    private static VeiculoHistoricoCidadeProjecao cidade(LocalDate data, String nome) {
        var cidade = mock(VeiculoHistoricoCidadeProjecao.class);
        when(cidade.getData()).thenReturn(data);
        when(cidade.getCidade()).thenReturn(nome);
        return cidade;
    }
}
