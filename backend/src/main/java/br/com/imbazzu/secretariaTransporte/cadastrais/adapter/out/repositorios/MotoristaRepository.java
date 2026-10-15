package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.repositorios;

import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.entidadesJpa.MotoristaJpa;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.Motorista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * Responsável interagir com o banco de dados
 */
public interface MotoristaRepository extends JpaRepository<MotoristaJpa, UUID> {

    @Query("SELECT m FROM MotoristaJpa m WHERE m.id =: id")
    Optional<Motorista> buscarPorId(@Param("id") UUID id);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdIgnoreCase(String cpf, UUID id);

    @Query("SELECT m FROM MotoristaJpa m WHERE m.nome LIKE CONCAT('%',:nome,'%') ")
    Page<MotoristaJpa> buscarPorNome(@Param("nome") String nome, Pageable pageable);
}
