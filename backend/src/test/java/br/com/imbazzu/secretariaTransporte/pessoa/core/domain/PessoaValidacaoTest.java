package br.com.imbazzu.secretariaTransporte.pessoa.core.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.RegraDeNegocioException;
import br.com.imbazzu.secretariaTransporte.especificacao.core.domain.Especificacao;

class PessoaValidacaoTest {

    @Test
    void exigeRetiradaDaValidacaoAntesDeEditar() {
        var pessoa = Pessoa.criar(new Nome("Ana Souza"), null,
                Especificacao.criar("Padrão", java.util.UUID.randomUUID(), null),
                false, 0, null, null, new Telefone("11987654321"));
        pessoa.validar();
        assertTrue(pessoa.isValidada());

        assertThrows(RegraDeNegocioException.class, () -> pessoa.editar(new Nome("Ana Souza Atualizada"),
                null, pessoa.getEspecificacao(), true, 0, null, null, new Telefone("11987654321")));
        assertTrue(pessoa.isValidada());
        assertFalse(pessoa.isBuscarNaResidencia());
        assertThrows(RegraDeNegocioException.class, pessoa::arquivar);
        assertThrows(RegraDeNegocioException.class, pessoa::desarquivar);
        assertThrows(RegraDeNegocioException.class, pessoa::garantirNaoValidada);

        pessoa.retirarValidacao();
        pessoa.editar(new Nome("Ana Souza Atualizada"), null, pessoa.getEspecificacao(),
                true, 0, null, null, new Telefone("11987654321"));
        assertFalse(pessoa.isValidada());
        assertTrue(pessoa.isBuscarNaResidencia());
    }
}
