package br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.out;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.Motorista;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;

import java.util.Optional;
import java.util.UUID;

public interface MotoristaRepositorioPorta {

    UUID salvar(Motorista motorista);

    boolean verificarExistenciaCpf(String cpf);

    boolean verificarExistenciaCpfIgnorandoId(String cpf, UUID id);

    Optional<Motorista> buscarPorId(UUID id);

    ResultadoPaginado<Motorista> buscarPorNome(String nome, int pagina, int tamanho);

}
