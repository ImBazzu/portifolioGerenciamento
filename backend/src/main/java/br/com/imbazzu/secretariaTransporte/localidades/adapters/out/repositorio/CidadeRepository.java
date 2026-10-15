package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.entiadeJpa.CidadeJpa;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.CidadeEstadoEnum;

import java.util.UUID;

/**
 * Integração com o banco de dados
 */
public interface CidadeRepository extends JpaRepository<CidadeJpa, UUID> {

    /**
     * Procurado no banco de dados pelo nome
     * 
     * @param nome     nome procurado
     * @param pageable config da pagina
     * @return retorna a lista da correspondente da cidade
     */
    @Query("""
            SELECT c FROM CidadeJpa c WHERE c.nome LIKE CONCAT('%',:nome,'%')""")
    Page<CidadeJpa> findByNome(@Param("nome") String nome, Pageable pageable);

    boolean existsByNomeAndEstadoAndIdNot(String nome, CidadeEstadoEnum estado, UUID id);

    boolean existsByNomeAndEstado(String nome, CidadeEstadoEnum estado);

    boolean existsById(UUID id);
}
