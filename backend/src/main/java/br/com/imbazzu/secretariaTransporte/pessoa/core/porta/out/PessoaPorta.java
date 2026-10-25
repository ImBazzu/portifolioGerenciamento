package br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out;

import java.util.Optional;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;

public interface PessoaPorta {

    UUID salvar(Pessoa pessoa);

    Optional<Pessoa> buscarPorId(UUID id);

    ResultadoPaginado<Pessoa> listar(String nome, boolean arquivado, int pagina, int tamanho);

    boolean existeCpf(String cpf, UUID ignorarId);

    void deletar(UUID id);
}
