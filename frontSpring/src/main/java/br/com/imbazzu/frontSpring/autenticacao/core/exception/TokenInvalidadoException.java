package br.com.imbazzu.frontspring.autenticacao.core.exception;

public class TokenInvalidadoException extends RuntimeException {

    public TokenInvalidadoException() {

        super("Token informado invalido.");
    }

}
