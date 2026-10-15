package br.com.imbazzu.secretariaTransporte.localidades.application.query.cidade.listarArquivado;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta;

import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta.CidadeOutputArquivado;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CidadeQueryListarArquivadoHandler {
    
    private final CidadeQueryPorta porta;

    public ResultadoPaginado<CidadeOutputArquivado> handler(CidadeQueryListarArquivado query) {
        return porta.listarArquivados(query.nome(), query.pagina(), query.tamanhoPagina());
    }
}
