package br.com.imbazzu.secretariaTransporte.especificacao.application.query.buscar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.jpaAdapter.EspecificacaoQueryAdapter;
import br.com.imbazzu.secretariaTransporte.especificacao.core.domain.Especificacao;
import br.com.imbazzu.secretariaTransporte.especificacao.core.excecoes.EspecificacaoNaoEncontradoException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EspecificacaoQueryBuscarHandler {

    private final EspecificacaoQueryAdapter adapter;

    @Transactional(readOnly = true)
    public Especificacao handler(EspecificacaoQueryBuscar query) {
        return adapter.buscar(query.id())
                .orElseThrow(() -> new EspecificacaoNaoEncontradoException(query.id()));
    }

    @Transactional(readOnly = true)
    public Especificacao buscarArquivada(EspecificacaoQueryBuscar query) {
        return adapter.buscarArquivado(query.id())
                .orElseThrow(() -> new EspecificacaoNaoEncontradoException(query.id()));
    }
}
