package br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.mapper;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Cpf;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Endereco;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.entidadeJpa.PessoaJpa;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;

public final class PessoaBancoMapper {

    private PessoaBancoMapper() {
    }

    public static Pessoa paraDomain(PessoaJpa entidade) {
        return Pessoa.recriar(
                entidade.getId(),
                new Nome(entidade.getNome()),
                observacaoOpcional(entidade.getObservacao()),
                entidade.getPrioridade(),
                entidade.getDataArquivado(),
                new Endereco(entidade.getEndereco()),
                new Cpf(entidade.getCpf()),
                new Telefone(entidade.getTelefone()));
    }

    public static void atualizarEntidade(Pessoa dominio, PessoaJpa entidade) {
        entidade.setId(dominio.getId());
        entidade.setNome(dominio.getNome().valor());
        entidade.setObservacao(dominio.getObservacao() == null ? null : dominio.getObservacao().valor());
        entidade.setPrioridade(dominio.getPrioridade());
        entidade.setDataArquivado(dominio.getDataArquivado());
        entidade.setEndereco(dominio.getEndereco().valor());
        entidade.setCpf(dominio.getCpf().valor());
        entidade.setTelefone(dominio.getTelefone().valor());
    }

    private static Observacao observacaoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : new Observacao(valor);
    }
}
