package br.com.imbazzu.secretariaTransporte.especificacao.application.command.desarquivar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.jpaAdapter.EspecificacaoCommandAdapter;
import br.com.imbazzu.secretariaTransporte.especificacao.core.excecoes.EspecificacaoNaoEncontradoException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EspecificacaoCommandDesarquivarHandler {

    private final EspecificacaoCommandAdapter adapter;

    @Transactional
    public void handler(EspecificacaoCommandDesarquivar command) {
        var especificacao = adapter.buscarPorId(command.id())
                .orElseThrow(() -> new EspecificacaoNaoEncontradoException(command.id()));

        especificacao.desarquivar();
        adapter.salvar(especificacao);
    }
}
