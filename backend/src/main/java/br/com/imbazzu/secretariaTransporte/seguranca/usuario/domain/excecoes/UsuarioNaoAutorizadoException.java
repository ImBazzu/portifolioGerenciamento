package br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.excecoes;

public class UsuarioNaoAutorizadoException extends RuntimeException {
    public UsuarioNaoAutorizadoException(String message) {
        super(message);
    }
}
