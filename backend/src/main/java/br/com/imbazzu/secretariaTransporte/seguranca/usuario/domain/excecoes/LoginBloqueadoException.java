package br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.excecoes;

public class LoginBloqueadoException extends RuntimeException {
    public LoginBloqueadoException(String message) {
        super(message);
    }
}
