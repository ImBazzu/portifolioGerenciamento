package br.com.imbazzu.secretariaTransporte.motorista;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface CategoriaMotoristaRepository extends JpaRepository<CategoriaMotoristaJpa, UUID> {
    List<CategoriaMotoristaJpa> findAllByOrderByNomeAsc();
    List<CategoriaMotoristaJpa> findByDataArquivadoIsNullOrderByNomeAsc();
    List<CategoriaMotoristaJpa> findByDataArquivadoIsNotNullOrderByNomeAsc();
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, UUID id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CategoriaMotoristaJpa c WHERE c.id = :id")
    Optional<CategoriaMotoristaJpa> bloquear(@Param("id") UUID id);
}
