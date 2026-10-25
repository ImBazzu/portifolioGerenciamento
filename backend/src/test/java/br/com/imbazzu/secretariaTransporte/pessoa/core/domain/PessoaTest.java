package br.com.imbazzu.secretariaTransporte.pessoa.core.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Cpf;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Endereco;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;

class PessoaTest {

    @Test
    void criaPessoaEPermiteArquivarEDesarquivar() {
        var pessoa = Pessoa.criar(
                new Nome("Pessoa teste"),
                new Observacao("Observação válida"),
                EnumPrioridade.ALTA,
                new Endereco("Rua das Flores, 123"),
                new Cpf("529.982.247-25"),
                new Telefone("(11) 98765-4321"));

        assertNotNull(pessoa.getId());
        assertEquals(EnumPrioridade.ALTA, pessoa.getPrioridade());
        assertEquals("52998224725", pessoa.getCpf().valor());
        assertEquals("11987654321", pessoa.getTelefone().valor());
        assertNull(pessoa.getDataArquivado());

        pessoa.arquivar();
        assertNotNull(pessoa.getDataArquivado());
        pessoa.desarquivar();
        assertNull(pessoa.getDataArquivado());
    }
}
