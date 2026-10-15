package br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.arquivar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.CidadeNaoEncontradaException;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CidadeCommandArquivarHandler {

    private final CidadeCommandPorta porta;

    public void handler(CidadeCommandArquivar command) {
        var cidade = porta.buscarPorId(command.id()).orElseThrow(
                () -> new CidadeNaoEncontradaException(command.id()));
        cidade.arquivar();
        porta.salvar(cidade);
    }
}
