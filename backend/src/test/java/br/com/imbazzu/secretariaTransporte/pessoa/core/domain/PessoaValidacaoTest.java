package br.com.imbazzu.secretariaTransporte.pessoa.core.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.especificacao.core.domain.Especificacao;

class PessoaValidacaoTest {

    @Test
    void edicaoDesmarcaValidacaoConfirmada() {
        var pessoa = Pessoa.criar(new Nome("Ana Souza"), null,
                Especificacao.criar("Padrão", java.util.UUID.randomUUID(), null),
                false, 0, null, null, new Telefone("11987654321"));
        pessoa.validar();
        assertTrue(pessoa.isValidada());

        pessoa.editar(new Nome("Ana Souza Atualizada"), null, pessoa.getEspecificacao(),
                true, 0, null, null, new Telefone("11987654321"));

        assertFalse(pessoa.isValidada());
        assertTrue(pessoa.isBuscarNaResidencia());
    }
}
