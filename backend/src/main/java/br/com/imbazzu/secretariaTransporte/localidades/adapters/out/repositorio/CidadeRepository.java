package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.entiadeJpa.CidadeJpa;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.projecoes.cidade.CidadeProjecoes;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.projecoes.cidade.CidadeProjecoesArquivado;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.CidadeEstadoEnum;

import java.util.Optional;
import java.util.UUID;

/**
 * Integração com o banco de dados
 */
public interface CidadeRepository extends JpaRepository<CidadeJpa, UUID> {

   
    @Query("""
            SELECT 
                c.id AS id, 
                c.nome AS nome, 
                c.estado AS estado, 
                c.tempoViagem AS tempoViagem, 
                COUNT(d) AS totalDestinos
            FROM CidadeJpa c 
            LEFT JOIN DestinoJpa d ON c.id = d.cidadeId
            WHERE c.id = :id AND c.dataArquivado IS NULL
            GROUP BY c.id, c.nome, c.estado, c.tempoViagem""")
    Optional<CidadeProjecoes>  buscarPorId(@Param("id") UUID id);

    @Query ("""
            SELECT 
                c.id AS id, 
                c.nome AS nome, 
                c.estado AS estado, 
                c.tempoViagem AS tempoViagem, 
                COUNT(d) AS totalDestinos,
                c.dataArquivado AS dataArquivamento
            FROM CidadeJpa c 
            LEFT JOIN DestinoJpa d ON c.id = d.cidadeId
            WHERE c.id = :id AND c.dataArquivado IS NOT NULL
            GROUP BY c.id, c.nome, c.estado, c.tempoViagem, c.dataArquivado""")
    Optional<CidadeProjecoesArquivado> buscarPorIdArquivado(@Param("id") UUID id);


    @Query(value = """
        SELECT 
                c.id AS id, 
                c.nome AS nome, 
                c.estado AS estado, 
                c.tempoViagem AS tempoViagem, 
                COUNT(d) AS totalDestinos
            FROM CidadeJpa c 
            LEFT JOIN DestinoJpa d ON c.id = d.cidadeId
            WHERE c.dataArquivado IS NULL
                AND (:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
            GROUP BY c.id, c.nome, c.estado, c.tempoViagem""",
            countQuery = """
            SELECT COUNT(c) FROM CidadeJpa c
            WHERE c.dataArquivado IS NULL
                AND (:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
            """)
    Page<CidadeProjecoes> listarCidades(@Param("nome") String nome, Pageable pageable);

    @Query(value="""
        SELECT 
                c.id AS id, 
                c.nome AS nome, 
                c.estado AS estado, 
                c.tempoViagem AS tempoViagem, 
                COUNT(d) AS totalDestinos,
                c.dataArquivado AS dataArquivamento
            FROM CidadeJpa c 
            LEFT JOIN DestinoJpa d ON c.id = d.cidadeId
            WHERE (:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND c.dataArquivado IS NOT NULL
            GROUP BY c.id, c.nome, c.estado, c.tempoViagem, c.dataArquivado""",
            countQuery = """
            SELECT COUNT(c) FROM CidadeJpa c
            WHERE (:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND c.dataArquivado IS NOT NULL
            """)
    Page<CidadeProjecoesArquivado> listarCidadesArquivadas(@Param("nome") String nome, Pageable pageable);

    boolean existsByNomeAndEstadoAndIdNot(String nome, CidadeEstadoEnum estado, UUID id);

    boolean existsByNomeAndEstado(String nome, CidadeEstadoEnum estado);

    boolean existsById(UUID id);
}
