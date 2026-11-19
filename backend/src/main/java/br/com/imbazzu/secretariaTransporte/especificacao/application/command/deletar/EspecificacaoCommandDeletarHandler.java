package br.com.imbazzu.secretariaTransporte.especificacao.application.command.deletar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.jpaAdapter.EspecificacaoCommandAdapter;
import br.com.imbazzu.secretariaTransporte.especificacao.core.excecoes.EspecificacaoNaoEncontradoException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EspecificacaoCommandDeletarHandler {

    private final EspecificacaoCommandAdapter adapter;

    @Transactional
    public void handler(EspecificacaoCommandDeletar command) {
        if (!adapter.buscarPorId(command.id()).isPresent()) {
            throw new EspecificacaoNaoEncontradoException(command.id());
        }
        adapter.deletar(command.id());
    }
}
