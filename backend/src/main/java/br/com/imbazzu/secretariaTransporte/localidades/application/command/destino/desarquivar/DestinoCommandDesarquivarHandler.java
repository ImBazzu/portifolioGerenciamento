package br.com.imbazzu.secretariaTransporte.localidades.application.command.destino.desarquivar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.DestinoNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DestinoCommandDesarquivarHandler {

    private final DestinoCommandPorta porta;

    public void handler(DestinoCommandDesarquivar command) {
        var destino = porta.buscarPorId(command.id()).orElseThrow(
                () -> new DestinoNaoEncontradoException(command.id()));
        destino.desarquivar();
        porta.salvar(destino);
    }
}
