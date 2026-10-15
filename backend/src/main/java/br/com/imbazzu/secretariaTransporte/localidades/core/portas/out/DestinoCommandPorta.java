package br.com.imbazzu.secretariaTransporte.localidades.core.portas.out;

import java.util.Optional;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.localidades.core.domain.Destino;

public interface DestinoCommandPorta {

    UUID salvar(Destino destino);

    Optional<Destino> buscarPorId(UUID idDestino);

    void deletar(UUID id);

}
