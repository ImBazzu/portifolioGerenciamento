package br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;

class ObservacaoTest {

    @Test
    void aceitaObservacaoEntreCincoE249Caracteres() {
        assertEquals("12345", new Observacao("12345").valor());
        assertEquals(249, new Observacao("a".repeat(249)).valor().length());
    }

    @Test
    void rejeitaObservacaoForaDoLimite() {
        assertThrows(DadosInvalidosException.class, () -> new Observacao("1234"));
        assertThrows(DadosInvalidosException.class, () -> new Observacao("a".repeat(250)));
    }
}
