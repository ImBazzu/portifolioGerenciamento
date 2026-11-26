package br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
    Optional<UsuarioJpaEntity> findByNome(String nome);
    boolean existsByNome(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, UUID id);
    Page<UsuarioJpaEntity> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM UsuarioJpaEntity u WHERE u.id = :id")
    Optional<UsuarioJpaEntity> buscarParaAtualizacao(@Param("id") UUID id);
}
