package br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
    Optional<UsuarioJpaEntity> findByNome(String nome);
    boolean existsByNome(String nome);
}
