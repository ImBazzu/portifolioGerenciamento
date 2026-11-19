package br.com.imbazzu.frontSpring.especificacao.core.excecoes;

import java.util.UUID;

public class EspecificacaoNaoEncontradaException extends RuntimeException {
    public EspecificacaoNaoEncontradaException(UUID id) {
        super("Especificação com ID " + id + " não encontrada.");
    }

    public EspecificacaoNaoEncontradaException() {
        super("Especificação não encontrada.");
    }
}
