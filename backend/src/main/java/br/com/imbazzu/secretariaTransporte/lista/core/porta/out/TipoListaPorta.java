package br.com.imbazzu.secretariaTransporte.lista.core.porta.out;

import java.util.Optional;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.lista.core.domain.Lista;

public interface TipoListaPorta {

    UUID salvar(Lista lista);

    Optional<Lista> buscarPorId(UUID id);

    void deletar(UUID id);
}
