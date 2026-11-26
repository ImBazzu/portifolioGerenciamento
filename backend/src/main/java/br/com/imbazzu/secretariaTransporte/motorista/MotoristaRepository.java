package br.com.imbazzu.secretariaTransporte.motorista;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.repository.query.Param;

public interface MotoristaRepository extends JpaRepository<MotoristaJpa, UUID> {
    boolean existsByTelefoneAndIdNot(String telefone, UUID id);
    boolean existsByCategoriaId(UUID categoriaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM MotoristaJpa m WHERE m.id = :id")
    Optional<MotoristaJpa> bloquear(@Param("id") UUID id);

    @Query("""
            SELECT m FROM MotoristaJpa m JOIN FETCH m.categoria
            WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
              AND (:categoria IS NULL OR m.categoria.id = :categoria)
              AND ((:arquivados = true AND m.dataArquivado IS NOT NULL)
                   OR (:arquivados = false AND m.dataArquivado IS NULL))
            ORDER BY m.nome, m.id
            """)
    Page<MotoristaJpa> buscar(@Param("nome") String nome, @Param("categoria") UUID categoria,
            @Param("arquivados") boolean arquivados, Pageable pageable);
}
