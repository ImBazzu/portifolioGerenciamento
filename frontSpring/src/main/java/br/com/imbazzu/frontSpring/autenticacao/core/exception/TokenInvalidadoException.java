package br.com.imbazzu.frontSpring.autenticacao.core.exception;

public class TokenInvalidadoException extends RuntimeException {

    public TokenInvalidadoException() {

        super("Token informado invalido.");
    }

}
