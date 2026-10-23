package br.com.imbazzu.secretariaTransporte.lista.adpter.out.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.entidadeJpa.TipoListaJpa;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.EnumPrioridade;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.TipoLista;

class TipoListaBancoMapperTest {

    @Test
    void converteListaERegrasDeCadastroSeparacaoNosDoisSentidos() {
        var cidade = UUID.randomUUID();
        var veiculo = UUID.randomUUID();
        var tipoVeiculo = UUID.randomUUID();
        var lista = TipoLista.criar(new Nome("Lista teste"), null);
        lista.criarRegraCadastro(
                List.of(veiculo),
                List.of(cidade),
                List.of(EnumPrioridade.ALTA),
                LocalTime.of(20, 0),
                LocalTime.of(6, 0),
                true,
                null);
        lista.criarRegraSeparacao(tipoVeiculo, Duration.ofNanos(1_500_000_001));

        var entidade = new TipoListaJpa();
        TipoListaBancoMapper.atualizarEntidade(lista, entidade);
        var recriada = TipoListaBancoMapper.paraDomain(entidade);
        TipoListaBancoMapper.atualizarEntidade(recriada, entidade);

        assertEquals(lista.getId(), recriada.getId());
        assertEquals(1, entidade.getRegrasCadastro().size());
        assertEquals(1, entidade.getRegrasSeparacao().size());
        assertNull(recriada.getDescricao());
        assertEquals(veiculo, recriada.getListaRegrasCadastro().getFirst().getIdsVeiculos().getFirst());
        assertEquals(cidade, recriada.getListaRegrasCadastro().getFirst().listaCidades().getFirst());
        assertEquals(EnumPrioridade.ALTA, recriada.getListaRegrasCadastro().getFirst().getPrioridades().getFirst());
        assertEquals(Duration.ofNanos(1_500_000_001),
                recriada.getListaRegraSeparacao().getFirst().getTempoTolerancia());
    }
}
