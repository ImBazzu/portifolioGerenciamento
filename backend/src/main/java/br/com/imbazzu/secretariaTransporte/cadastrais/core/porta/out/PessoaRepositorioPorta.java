package br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.out;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.Pessoa;

import java.util.Optional;
import java.util.UUID;

public interface PessoaRepositorioPorta {

    Optional<Pessoa> buscarPorId(UUID idPessoa);

    ResultadoPaginado<Pessoa> buscarPorNome(String nome, int pagina, int tamanho);

    UUID salvar(Pessoa pessoa);

    boolean verificarExistenciaCpf(String cpf);

    boolean verificarExistenciaCpfIgnorandoId(String cpf, UUID idPessoa);
}
