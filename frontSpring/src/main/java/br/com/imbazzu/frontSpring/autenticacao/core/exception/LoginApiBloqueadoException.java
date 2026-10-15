package br.com.imbazzu.frontspring.autenticacao.core.exception;

public class LoginApiBloqueadoException extends RuntimeException {
    public LoginApiBloqueadoException() {
        super("Usuário bloqueador por diversas tentativas. Tente novamente em 15 minutos");
    }
}
