package br.com.imbazzu.secretariaTransporte.localidades.application.query.cidade.listar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta.CidadeOutput;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CidadeQueryListarHandler {
    
    private final CidadeQueryPorta porta;

    public ResultadoPaginado<CidadeOutput> handler(CidadeQueryListar query) {
        return porta.listar(query.nome(), query.pagina(), query.tamanhoPagina());
    }
}
