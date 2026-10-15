package br.com.imbazzu.secretariaTransporte.localidades.application.command.destino.deletar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DestinoCommandDeletarHandler {

    private final DestinoCommandPorta porta;

    public void handler(DestinoCommandDeletar command) {
        porta.deletar(command.id());
    }
}
