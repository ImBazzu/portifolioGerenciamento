package br.com.imbazzu.secretariaTransporte.lista.core.porta.out;

import java.util.Optional;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.lista.core.domain.TipoLista;

public interface TipoListaPorta {

    UUID salvar(TipoLista lista);

    Optional<TipoLista> buscarPorId(UUID id);

    void deletar(UUID id);
}
