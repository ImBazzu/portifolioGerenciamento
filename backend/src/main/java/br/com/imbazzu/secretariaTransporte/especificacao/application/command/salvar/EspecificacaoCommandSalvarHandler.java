package br.com.imbazzu.secretariaTransporte.especificacao.application.command.salvar;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.jpaAdapter.EspecificacaoCommandAdapter;
import br.com.imbazzu.secretariaTransporte.especificacao.core.domain.Especificacao;
import br.com.imbazzu.secretariaTransporte.especificacao.core.excecoes.EspecificacaoDuplicadoException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EspecificacaoCommandSalvarHandler {

    private final EspecificacaoCommandAdapter adapter;

    @Transactional
    public UUID handler(EspecificacaoCommandSalvar command) {
        if (adapter.existeNome(command.nome(), null)) {
            throw new EspecificacaoDuplicadoException(command.nome());
        }

        var especificacao = Especificacao.criar(command.nome(), command.prioridade(), command.observacao());
        return adapter.salvar(especificacao);
    }
}
