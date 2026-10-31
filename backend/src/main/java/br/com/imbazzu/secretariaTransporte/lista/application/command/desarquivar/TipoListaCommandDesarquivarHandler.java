package br.com.imbazzu.secretariaTransporte.lista.application.command.desarquivar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.lista.core.excecoes.TipoListaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.lista.core.porta.out.TipoListaPorta;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class TipoListaCommandDesarquivarHandler {
    
    private final TipoListaPorta porta;

    @Transactional
    public void handler(TipoListaCommandDesarquivar command) {
        var lista = porta.buscarPorId(command.id()).orElseThrow(
            ()->new TipoListaNaoEncontradoException(command.id())
        );
        lista.desarquivar();
        porta.salvar(lista);
    }
}
