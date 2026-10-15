package br.com.imbazzu.secretariaTransporte.localidades.application.query.cidade.buscar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.CidadeNaoEncontradaException;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta.CidadeOutput;
import lombok.RequiredArgsConstructor;


@Service 
@RequiredArgsConstructor 
public class CidadeQueryBuscarHandler {
    
    private final CidadeQueryPorta porta;

    public CidadeOutput handler(CidadeQueryBuscar query) {
        return porta.buscar(query.id())
        .orElseThrow(() -> new CidadeNaoEncontradaException(query.id()));
    }
}
