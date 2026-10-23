package br.com.imbazzu.secretariaTransporte.lista.core.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;

class RegraSeparacaoTest {

    @Test
    void naoPermiteToleranciaNegativaAoCriarOuEditar() {
        var idLista = UUID.randomUUID();
        var idTipoVeiculo = UUID.randomUUID();

        assertThrows(DadosInvalidosException.class,
                () -> RegraSeparacao.criar(idLista, idTipoVeiculo, Duration.ofSeconds(-1)));

        var regra = RegraSeparacao.criar(idLista, idTipoVeiculo, Duration.ZERO);
        var outroTipoVeiculo = UUID.randomUUID();

        assertThrows(DadosInvalidosException.class,
                () -> regra.editar(outroTipoVeiculo, Duration.ofSeconds(-1)));
        assertEquals(idTipoVeiculo, regra.getIdTipoVeiculo());
        assertEquals(Duration.ZERO, regra.getTempoTolerancia());
    }
}
