package br.com.imbazzu.secretariaTransporte.localidades.core.portas.out;

import java.util.Optional;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.localidades.core.domain.Cidade;

public interface CidadeCommandPorta {

    UUID salvar(Cidade cidade);

    void deletar(UUID cidade);

    Optional<Cidade> buscarPorId(UUID idCidade);
}
