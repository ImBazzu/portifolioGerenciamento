package br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Cpf;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Endereco;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.entidadeJpa.PessoaJpa;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;

class PessoaBancoMapperTest {

    @Test
    void convertePessoaNoJpaEDeVolta() {
        var pessoa = Pessoa.criar(
                new Nome("Pessoa teste"),
                new Observacao("Observação válida"),
                EnumPrioridade.URGENTE,
                new Endereco("Rua das Flores, 123"),
                new Cpf("52998224725"),
                new Telefone("11987654321"));
        var entidade = new PessoaJpa();

        PessoaBancoMapper.atualizarEntidade(pessoa, entidade);
        var recriada = PessoaBancoMapper.paraDomain(entidade);

        assertEquals(pessoa.getId(), recriada.getId());
        assertEquals(pessoa.getNome(), recriada.getNome());
        assertEquals(pessoa.getObservacao(), recriada.getObservacao());
        assertEquals(pessoa.getPrioridade(), recriada.getPrioridade());
        assertEquals(pessoa.getEndereco(), recriada.getEndereco());
        assertEquals(pessoa.getCpf(), recriada.getCpf());
        assertEquals(pessoa.getTelefone(), recriada.getTelefone());

        entidade.setObservacao(null);
        assertNull(PessoaBancoMapper.paraDomain(entidade).getObservacao());
    }
}
