package br.com.imbazzu.secretariaTransporte.localidades.application.command.destino.deletar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DestinoCommandDeletarHandler {

    private final DestinoCommandPorta porta;

    @Transactional
    public void handler(DestinoCommandDeletar command) {
        porta.deletar(command.id());
    }
}
