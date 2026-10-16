package br.com.imbazzu.frontSpring.autenticacao.core.exception;

public class RefreshTokenInvalidoException extends RuntimeException {
    public RefreshTokenInvalidoException() {
        super("Refresh token invalido");
    }
}
