package br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out;

import java.util.Optional;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;

public interface PessoaCommandPorta {

    UUID salvar(Pessoa pessoa);

    Optional<Pessoa> buscarPorId(UUID id);

    boolean existeCpf(String cpf, UUID ignorarId);


    void deletar(UUID id);

}
