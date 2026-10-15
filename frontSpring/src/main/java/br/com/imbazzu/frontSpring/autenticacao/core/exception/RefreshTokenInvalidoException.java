package br.com.imbazzu.frontspring.autenticacao.core.exception;

public class RefreshTokenInvalidoException extends RuntimeException {
    public RefreshTokenInvalidoException() {
        super("Refresh token invalido");
    }
}
