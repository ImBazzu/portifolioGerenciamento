package br.com.imbazzu.secretariaTransporte.lista.application.command.deletar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.lista.core.excecoes.TipoListaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.lista.core.porta.out.TipoListaPorta;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class TipoListaCommandDeletarHandler {
    
    private final TipoListaPorta porta;

    public void handler(TipoListaCommandDeletar command){
        porta.buscarPorId(command.id()).orElseThrow(
            ()->new TipoListaNaoEncontradoException(command.id())
        );
        porta.deletar(command.id());
    }
}
