package br.com.imbazzu.frontSpring.autenticacao.core.exception;


public class LoginApiException extends RuntimeException {
    public LoginApiException() {
        super("Usuario ou senha inválidos");
    }

}
