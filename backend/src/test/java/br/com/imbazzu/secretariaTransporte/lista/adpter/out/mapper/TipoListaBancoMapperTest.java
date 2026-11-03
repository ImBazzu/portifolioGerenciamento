package br.com.imbazzu.secretariaTransporte.lista.adpter.out.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.lista.adpter.out.entidadeJpa.TipoListaJpa;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.TipoLista;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;

class TipoListaBancoMapperTest {

    @Test
    void criaAssociacaoDeTipoVeiculoAoAdicionarRegraDeSeparacao() {
        var idTipoVeiculo = UUID.randomUUID();
        var lista = TipoLista.criar(new Nome("Saude"), null);
        lista.criarRegraSeparacao(idTipoVeiculo, Duration.ofHours(1));
        var entidade = new TipoListaJpa();

        TipoListaBancoMapper.atualizarEntidade(lista, entidade);

        var regra = entidade.getRegrasSeparacao().getFirst();
        assertNotNull(regra.getTipoVeiculo());
        assertEquals(idTipoVeiculo, regra.getTipoVeiculo().getId());
        assertEquals(3_600, regra.getTempoToleranciaSegundos());
    }
}
