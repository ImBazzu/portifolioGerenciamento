package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.mapper;

import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.entidadesJpa.PessoaJpa;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Cpf;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.Pessoa;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Endereco;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;

public class PessoaBancoMapper {

    public static PessoaJpa paraEntityJpa(Pessoa pessoa) {
        return new PessoaJpa(pessoa.getId(),pessoa.getNome().valor(),
                pessoa.getCpf().valor(),pessoa.getPessoaCondicaoId(),
                pessoa.getTelefone().valor(),pessoa.getEndereco().valor(),
                pessoa.getObservacao().valor(),pessoa.isArquivado());
    }

    public static Pessoa paraDomainPessoa(PessoaJpa pessoa) {
        return Pessoa.recriar(pessoa.getId(),new Nome(pessoa.getNome()),
                new Cpf(pessoa.getCpf()),new Telefone(pessoa.getTelefone()),pessoa.getPessoaCondicaoId(),
                new Endereco(pessoa.getEndereco()),new Observacao(pessoa.getObservacao()),pessoa.isArquivado());
    }
}
