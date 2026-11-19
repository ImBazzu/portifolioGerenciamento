package br.com.imbazzu.secretariaTransporte.prioridade;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrioridadeRepository extends JpaRepository<PrioridadeJpa, UUID> {
    interface PrioridadeComTotal {
        PrioridadeJpa getPrioridade();
        long getTotal();
    }

    @Query("select p as prioridade, count(e) as total from PrioridadeJpa p "
            + "left join EspecificacaoJpa e on e.prioridade = p.id group by p order by p.nome")
    List<PrioridadeComTotal> listarComTotalEspecificacoes();

    @Query("select count(e) from EspecificacaoJpa e where e.prioridade = :id")
    long contarEspecificacoes(@Param("id") UUID id);

    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, UUID id);

    @Query("select count(e) > 0 from EspecificacaoJpa e where e.prioridade = :id")
    boolean possuiEspecificacoes(@Param("id") UUID id);

    @Query("select count(r) > 0 from RegraCadastroJpa r join r.prioridades p where p = :id")
    boolean possuiRegras(@Param("id") UUID id);
}
