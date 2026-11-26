package br.com.imbazzu.secretariaTransporte.motorista;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaMotoristaRepository extends JpaRepository<CategoriaMotoristaJpa, UUID> {
    List<CategoriaMotoristaJpa> findAllByOrderByNomeAsc();
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, UUID id);
}
