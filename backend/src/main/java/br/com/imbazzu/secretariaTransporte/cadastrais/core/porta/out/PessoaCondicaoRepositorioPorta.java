package br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.out;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.PessoaCondicao;

import java.util.Optional;
import java.util.UUID;

public interface PessoaCondicaoRepositorioPorta {

    Optional<PessoaCondicao> buscarPorId(UUID pessoaCondicaoId);

    UUID salvar(PessoaCondicao pessoaCondicao);

    ResultadoPaginado<PessoaCondicao> buscarPorNome(String nome, int pagina, int tamanho);

    boolean existePorNome(String nome);

    boolean existePorNomeIgnorandoId(String nome,UUID pessoaCondicaoId);
}
