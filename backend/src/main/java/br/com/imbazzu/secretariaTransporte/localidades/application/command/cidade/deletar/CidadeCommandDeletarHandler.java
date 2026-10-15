package br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.deletar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CidadeCommandDeletarHandler {

    private final CidadeCommandPorta porta;

    public void handler(CidadeCommandDeletar command) {

        porta.deletar(command.id());
    }
}
