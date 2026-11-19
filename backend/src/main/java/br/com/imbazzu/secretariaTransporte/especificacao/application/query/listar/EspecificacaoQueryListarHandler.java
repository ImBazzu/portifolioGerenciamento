package br.com.imbazzu.secretariaTransporte.especificacao.application.query.listar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.jpaAdapter.EspecificacaoQueryAdapter;
import br.com.imbazzu.secretariaTransporte.especificacao.core.domain.Especificacao;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EspecificacaoQueryListarHandler {

    private final EspecificacaoQueryAdapter adapter;

    @Transactional(readOnly = true)
    public ResultadoPaginado<Especificacao> handler(EspecificacaoQueryListar query) {
        return adapter.listarAtivas(query.nome(), query.pagina(), query.tamanho());
    }
}
