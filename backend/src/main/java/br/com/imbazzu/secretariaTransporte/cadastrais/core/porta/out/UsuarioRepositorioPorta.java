package br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.out;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositorioPorta {

    Optional<Usuario> buscarPorNome(String nome);
    boolean existePorNome(String nome);
    void salvar(Usuario usuario);
}
