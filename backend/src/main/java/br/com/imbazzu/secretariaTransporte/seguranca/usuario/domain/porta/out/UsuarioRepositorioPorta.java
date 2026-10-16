package br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out;

import java.util.Optional;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model.Usuario;

public interface UsuarioRepositorioPorta {

    Optional<Usuario> buscarPorNome(String nome);
    boolean existePorNome(String nome);
    void salvar(Usuario usuario);
}
