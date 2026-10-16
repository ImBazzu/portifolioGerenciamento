package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.entiadeJpa.DestinoJpa;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.projecoes.Destino.DestinoProjecoes;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.projecoes.Destino.DestinoProjecoesArquivado;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.projecoes.Destino.DestinoProjecoesHistorico;

public interface DestinoRepository extends JpaRepository<DestinoJpa, UUID> {
    @Query(value = """
            SELECT
                d.id as id,
                d.nome as nome,
                count(p.id) as qtdPassageiros
            FROM
                DestinoJpa d
            LEFT JOIN
                PassageiroJpa p ON d.id = p.destinoId
            WHERE
                d.nome like CONCAT('%',:nome,'%')
                AND d.cidadeId = :idCidade
                AND d.dataArquivado is null
            GROUP BY d.id, d.nome""", countQuery = """
            SELECT
                COUNT(d)
            FROM
                DestinoJpa d
            WHERE
                d.nome LIKE CONCAT('%', :nome, '%')
                AND d.cidadeId = :idCidade
                AND d.dataArquivado is null
            """)
    Page<DestinoProjecoes> listarPorCidade(
            @Param("idCidade") UUID idCidade, @Param("nome") String nome, Pageable pageable);

    @Query(value = """
            SELECT
                    d.id as id,
                    d.dataArquivado as dataArquivado,
                    d.nome as nome,
                    count(p.id) as qtdPassageiros
            FROM
                DestinoJpa d
            LEFT JOIN
                PassageiroJpa p ON d.id = p.destinoId
            WHERE d.nome like CONCAT('%',:nome,'%')
                AND
                d.cidadeId = :idCidade
                AND
                d.dataArquivado is not null
            GROUP BY d.id, d.nome, d.dataArquivado""", countQuery = """
            SELECT
                COUNT(d)
            FROM
                DestinoJpa d
            WHERE
                d.nome LIKE CONCAT('%', :nome, '%')
                AND
                d.cidadeId = :idCidade
                AND
                d.dataArquivado is not null
            """)

    Page<DestinoProjecoesArquivado> listarPorCidadeArquivado(
            @Param("idCidade") UUID idCidade, @Param("nome") String nome, Pageable pageable);

    @Query(value = """
                SELECT
                    v.dataSaida as data,
                    COUNT(DISTINCT p.id) as quantidade
                FROM DestinoJpa d
                JOIN PassageiroJpa p ON d.id = p.destinoId
                JOIN VeiculoJpa v ON p.veiculoId = v.id
                WHERE d.id = :id
                GROUP BY v.dataSaida
                ORDER BY v.dataSaida DESC
            """, countQuery = """
                SELECT COUNT(DISTINCT v.dataSaida)
                FROM DestinoJpa d 
                JOIN PassageiroJpa p ON d.id = p.destinoId 
                JOIN VeiculoJpa v on p.veiculoId = v.id
                WHERE d.id = :id
            """)
    Page<DestinoProjecoesHistorico> buscarHistorico(@Param("id") UUID id, Pageable pageable);

    @Query("""
            SELECT
                d.id as id,
                d.nome as nome,
                count(p.id) as qtdPassageiros
            FROM
                DestinoJpa d
            LEFT JOIN
                PassageiroJpa p ON d.id = p.destinoId
            WHERE
                d.id = :id
                AND d.dataArquivado IS NULL
            GROUP BY d.id, d.nome""")
    Optional<DestinoProjecoes> buscarPorId(@Param("id") UUID id);

    @Query("""
            SELECT
                d.id as id,
                d.nome as nome,
                d.dataArquivado as dataArquivado,
                count(p.id) as qtdViagens
            FROM
                DestinoJpa d
            LEFT JOIN
                PassageiroJpa p ON d.id = p.destinoId
            WHERE
                d.id = :id
                AND d.dataArquivado IS NOT NULL
            GROUP BY d.id, d.nome, d.dataArquivado""")
    Optional<DestinoProjecoesArquivado> buscarPorIdArquivado(@Param("id") UUID id);

    boolean existsByCidadeIdAndNome(@Param("idCidade") UUID idCidade, @Param("nome") String nome);

    boolean existsByCidadeIdAndNomeAndIdNot(@Param("cidadeId") UUID cidadeId, @Param("nome") String nome,
            @Param("id") UUID id);
}
