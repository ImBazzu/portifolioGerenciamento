package br.com.imbazzu.frontSpring.especificacao.core.excecoes;

public class EspecificacaoDuplicadaException extends RuntimeException {
    public EspecificacaoDuplicadaException() {
        super("Especificação com este nome já existe.");
    }

    public EspecificacaoDuplicadaException(String message) {
        super(message);
    }
}
