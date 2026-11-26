package br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.repositorio;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.entidadeJpa.PessoaJpa;

public interface PessoaRepository extends JpaRepository<PessoaJpa, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PessoaJpa p WHERE p.id = :id")
    Optional<PessoaJpa> buscarParaAlteracao(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"autor", "especificacao"})
    Optional<PessoaJpa> findByIdAndDataArquivadoIsNull(UUID id);

    @EntityGraph(attributePaths = {"autor", "especificacao"})
    @Query("""
        SELECT p FROM PessoaJpa p
        WHERE p.dataArquivado IS NOT NULL AND p.id = :id
        """)
    Optional<PessoaJpa> buscarArquivado(@Param("id") UUID id);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, UUID id);

    @EntityGraph(attributePaths = {"autor", "especificacao"})
    @Query("""
            SELECT p FROM PessoaJpa p
            WHERE p.dataArquivado IS NULL
              AND (:especificacaoId IS NULL OR p.especificacao.id = :especificacaoId)
              AND (:validada IS NULL OR p.validada = :validada)
              AND (
                    LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
                    OR (:numero <> '' AND (p.cpf LIKE CONCAT('%', :numero, '%')
                        OR p.telefone LIKE CONCAT('%', :numero, '%')))
                  )
            ORDER BY p.nome ASC
            """)
    Page<PessoaJpa> listarAtivas(
            @Param("nome") String nome, @Param("numero") String numero,
            @Param("especificacaoId") UUID especificacaoId, @Param("validada") Boolean validada, Pageable pageable);

    @EntityGraph(attributePaths = {"autor", "especificacao"})
    @Query("""
            SELECT p FROM PessoaJpa p
            WHERE p.dataArquivado IS NOT NULL
              AND (:especificacaoId IS NULL OR p.especificacao.id = :especificacaoId)
              AND (:validada IS NULL OR p.validada = :validada)
              AND (
                    LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
                    OR (:numero <> '' AND (p.cpf LIKE CONCAT('%', :numero, '%')
                        OR p.telefone LIKE CONCAT('%', :numero, '%')))
                  )
            ORDER BY p.nome ASC
            """)
    Page<PessoaJpa> listarArquivadas(
            @Param("nome") String nome, @Param("numero") String numero,
            @Param("especificacaoId") UUID especificacaoId, @Param("validada") Boolean validada, Pageable pageable);
}
