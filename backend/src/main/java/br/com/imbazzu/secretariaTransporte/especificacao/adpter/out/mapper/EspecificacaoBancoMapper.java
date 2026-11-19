package br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.mapper;

import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.entidadeJpa.EspecificacaoJpa;
import br.com.imbazzu.secretariaTransporte.especificacao.core.domain.Especificacao;

public final class EspecificacaoBancoMapper {

    private EspecificacaoBancoMapper() {
    }

    public static Especificacao paraDomain(EspecificacaoJpa entidade) {
        return Especificacao.recriar(
                entidade.getId(),
                entidade.getNome(),
                entidade.getPrioridade(),
                entidade.getObservacao(),
                entidade.getDataArquivado());
    }

    public static EspecificacaoJpa paraEntidade(Especificacao dominio) {
        EspecificacaoJpa entidade = new EspecificacaoJpa();
        entidade.setId(dominio.getId());
        entidade.setNome(dominio.getNome());
        entidade.setPrioridade(dominio.getPrioridade());
        entidade.setObservacao(dominio.getObservacao());
        entidade.setDataArquivado(dominio.getDataArquivado());
        return entidade;
    }

    public static void atualizarEntidade(Especificacao dominio, EspecificacaoJpa entidade) {
        entidade.setId(dominio.getId());
        entidade.setNome(dominio.getNome());
        entidade.setPrioridade(dominio.getPrioridade());
        entidade.setObservacao(dominio.getObservacao());
        entidade.setDataArquivado(dominio.getDataArquivado());
    }
}
