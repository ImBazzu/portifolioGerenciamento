package br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.excecoes;

public class UsuarioSemPermissaoException extends RuntimeException {
    public UsuarioSemPermissaoException(String message) {
        super(message);
    }
}
