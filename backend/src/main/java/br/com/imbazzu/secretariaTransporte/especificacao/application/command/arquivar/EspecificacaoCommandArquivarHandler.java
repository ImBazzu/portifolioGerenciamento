package br.com.imbazzu.secretariaTransporte.especificacao.application.command.arquivar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.jpaAdapter.EspecificacaoCommandAdapter;
import br.com.imbazzu.secretariaTransporte.especificacao.core.excecoes.EspecificacaoNaoEncontradoException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EspecificacaoCommandArquivarHandler {

    private final EspecificacaoCommandAdapter adapter;

    @Transactional
    public void handler(EspecificacaoCommandArquivar command) {
        var especificacao = adapter.buscarPorId(command.id())
                .orElseThrow(() -> new EspecificacaoNaoEncontradoException(command.id()));

        especificacao.arquivar();
        adapter.salvar(especificacao);
    }
}
