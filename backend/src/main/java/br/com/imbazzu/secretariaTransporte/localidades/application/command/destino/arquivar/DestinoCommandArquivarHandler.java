package br.com.imbazzu.secretariaTransporte.localidades.application.command.destino.arquivar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.DestinoNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DestinoCommandArquivarHandler {

    private final DestinoCommandPorta porta;

    @Transactional
    public void handler(DestinoCommandArquivar command) {
        var destino = porta.buscarPorId(command.id()).orElseThrow(
                () -> new DestinoNaoEncontradoException(command.id()));
        destino.arquivar();
        porta.salvar(destino);
    }
}
