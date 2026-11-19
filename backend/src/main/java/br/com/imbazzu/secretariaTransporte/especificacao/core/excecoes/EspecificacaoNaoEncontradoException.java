package br.com.imbazzu.secretariaTransporte.especificacao.core.excecoes;

import java.util.UUID;

public class EspecificacaoNaoEncontradoException extends RuntimeException {

    public EspecificacaoNaoEncontradoException(UUID id) {
        super("Especificação com ID %s não encontrada".formatted(id));
    }

    public EspecificacaoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
