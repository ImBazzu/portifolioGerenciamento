package br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.repositorio;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.entidadeJpa.PessoaJpa;

public interface PessoaRepository extends JpaRepository<PessoaJpa, UUID> {

    Optional<PessoaJpa> findByIdAndDataArquivadoIsNull(UUID id);

    Optional<PessoaJpa> findByIdAndDataArquivadoIsNotNull(UUID id);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, UUID id);

    @Query("""
            SELECT p FROM PessoaJpa p
            WHERE p.dataArquivado IS NULL
              AND (
                    LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
                    OR (:numero <> '' AND (p.cpf LIKE CONCAT('%', :numero, '%')
                        OR p.telefone LIKE CONCAT('%', :numero, '%')))
                  )
            ORDER BY p.nome ASC
            """)
    Page<PessoaJpa> listarAtivas(
            @Param("nome") String nome, @Param("numero") String numero, Pageable pageable);

    @Query("""
            SELECT p FROM PessoaJpa p
            WHERE p.dataArquivado IS NOT NULL
              AND (
                    LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
                    OR (:numero <> '' AND (p.cpf LIKE CONCAT('%', :numero, '%')
                        OR p.telefone LIKE CONCAT('%', :numero, '%')))
                  )
            ORDER BY p.nome ASC
            """)
    Page<PessoaJpa> listarArquivadas(
            @Param("nome") String nome, @Param("numero") String numero, Pageable pageable);
}
