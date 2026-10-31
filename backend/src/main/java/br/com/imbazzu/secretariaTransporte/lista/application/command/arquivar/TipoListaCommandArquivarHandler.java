package br.com.imbazzu.secretariaTransporte.lista.application.command.arquivar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.lista.core.excecoes.TipoListaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.lista.core.porta.out.TipoListaPorta;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class TipoListaCommandArquivarHandler {
    
    private final TipoListaPorta porta;

    @Transactional 
    public void handler(TipoListaCommandArquivar command){
        var lista = porta.buscarPorId(command.id()).orElseThrow(
            ()->new TipoListaNaoEncontradoException(command.id()));
        lista.arquivar();
        porta.salvar(lista);
        }


}
