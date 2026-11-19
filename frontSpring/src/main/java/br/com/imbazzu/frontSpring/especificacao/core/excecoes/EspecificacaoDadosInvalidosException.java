package br.com.imbazzu.frontSpring.especificacao.core.excecoes;

public class EspecificacaoDadosInvalidosException extends RuntimeException {
    public EspecificacaoDadosInvalidosException() {
        super("Dados inválidos para a especificação.");
    }

    public EspecificacaoDadosInvalidosException(String message) {
        super(message);
    }
}
