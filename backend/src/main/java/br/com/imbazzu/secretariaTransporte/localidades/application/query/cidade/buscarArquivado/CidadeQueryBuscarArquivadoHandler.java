package br.com.imbazzu.secretariaTransporte.localidades.application.query.cidade.buscarArquivado;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.CidadeNaoEncontradaException;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta.CidadeOutputArquivado;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CidadeQueryBuscarArquivadoHandler {
    
    private final CidadeQueryPorta porta;

    public CidadeOutputArquivado handler(CidadeQueryBuscarArquivado query) {
        return porta.buscarArquivado(query.id())
        .orElseThrow(() -> new CidadeNaoEncontradaException(query.id()));
    }
}
