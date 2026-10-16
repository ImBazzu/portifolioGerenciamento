package br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.persistencia;

import java.util.Optional;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model.Usuario;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.UsuarioRepositorioPorta;

public class UsuarioRepositorioAdapter implements UsuarioRepositorioPorta {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositorioAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Usuario> buscarPorNome(String nome) {
        return jpaRepository.findByNome(nome).map(entity -> entity.toDomain());
    }

    @Override
    public boolean existePorNome(String nome) {
        return jpaRepository.existsByNome(nome);
    }

    @Override
    public void salvar(Usuario usuario) {
        jpaRepository.save(UsuarioJpaEntity.fromDomain(usuario));
    }
}
