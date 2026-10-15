package br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.desarquivar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.CidadeNaoEncontradaException;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CidadeCommandDesarquivarHandler {

    private final CidadeCommandPorta porta;

    @Transactional
    public void handler(CidadeCommandDesarquivar command) {

        var cidade = porta.buscarPorId(command.id()).orElseThrow(
                () -> new CidadeNaoEncontradaException(command.id()));
        cidade.desarquivar();
        porta.salvar(cidade);
    }
}
