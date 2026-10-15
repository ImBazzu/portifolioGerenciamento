package br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.excecoes;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }
}
