package br.com.imbazzu.secretariaTransporte.lista.application.query.listarArquivado;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaResumo;
import br.com.imbazzu.secretariaTransporte.lista.core.porta.out.TipoListaPortaQuery;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListaQueryListarArquivadoHandler {

    private final TipoListaPortaQuery portaQuery;

    @Transactional(readOnly = true)
    public ResultadoPaginado<TipoListaResumo> handler(ListaQueryListarArquivado query) {
        return portaQuery.listarArquivados(query.nome(), query.pagina(), query.tamanho());
    }
}
