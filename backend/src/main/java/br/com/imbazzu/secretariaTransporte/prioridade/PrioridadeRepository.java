package br.com.imbazzu.secretariaTransporte.prioridade;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrioridadeRepository extends JpaRepository<PrioridadeJpa, UUID> {
    List<PrioridadeJpa> findAllByOrderByNomeAsc();
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, UUID id);

    @Query("select count(e) > 0 from EspecificacaoJpa e where e.prioridade = :id")
    boolean possuiEspecificacoes(@Param("id") UUID id);

    @Query("select count(r) > 0 from RegraCadastroJpa r join r.prioridades p where p = :id")
    boolean possuiRegras(@Param("id") UUID id);
}
