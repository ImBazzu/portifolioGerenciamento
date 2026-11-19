package br.com.imbazzu.secretariaTransporte.especificacao.application.command.editar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.jpaAdapter.EspecificacaoCommandAdapter;
import br.com.imbazzu.secretariaTransporte.especificacao.core.excecoes.EspecificacaoDuplicadoException;
import br.com.imbazzu.secretariaTransporte.especificacao.core.excecoes.EspecificacaoNaoEncontradoException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EspecificacaoCommandEditarHandler {

    private final EspecificacaoCommandAdapter adapter;

    @Transactional
    public void handler(EspecificacaoCommandEditar command) {
        var especificacao = adapter.buscarPorId(command.id())
                .orElseThrow(() -> new EspecificacaoNaoEncontradoException(command.id()));

        if (adapter.existeNome(command.nome(), command.id())) {
            throw new EspecificacaoDuplicadoException(command.nome());
        }

        especificacao.editar(command.nome(), command.prioridade(), command.observacao());
        adapter.salvar(especificacao);
    }
}
