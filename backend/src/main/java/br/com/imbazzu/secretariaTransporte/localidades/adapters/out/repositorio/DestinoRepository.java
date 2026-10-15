package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.entiadeJpa.DestinoJpa;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.projecoes.DestinoProjecoes;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.projecoes.DestinoProjecoesArquivado;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.projecoes.DestinoProjecoesHistorico;

import java.util.Optional;
import java.util.UUID;

public interface DestinoRepository extends JpaRepository<DestinoJpa, UUID> {
    @Query(value = """
            SELECT
                d.id as id,
                d.nome as nome,
                count(p.id) as qtdPassageiros
            FROM
                DestinoJpa d
            JOIN
                Passageiro p ON d.id = p.destinoId
            WHERE
                d.nome like CONCAT('%',:nome,'%')
                AND d.cidadeId = :idCidade
                AND d.dataArquivado is null
            GROUP BY d.id""", countQuery = """
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
            JOIN
                Passageiro p ON d.id = p.destinoId
            WHERE d.nome like CONCAT('%',:nome,'%')
                AND
                d.cidadeId = :idCidade
                AND
                d.dataArquivado is not null
            GROUP BY d.id""", countQuery = """
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
                    COUNT (UNIQUE p.id) as quantidade
                FROM DestinoJpa d
                JOIN PassageiroJpa p ON d.id = p.destinoId
                JOIN VeiculoJpa v ON p.veiculoId = v.id
                WHERE d.id = :id
                GROUP BY v.dataSaida
                ORDER BY v.dataSaida DESC
            """, countQuery = """
                SELECT COUNT(DISTINCT v.dataSaida)
                FROM DestinoJpa d JOIN d.passageiros p JOIN p.veiculo v
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
            JOIN
                Passageiro p ON d.id = p.destinoId
            WHERE
                d.id = :id
            GROUP BY d.id""")
    Optional<DestinoProjecoes> buscarPorId(@Param("id") UUID id);

    @Query("""
            SELECT
                d.id as id,
                d.nome as nome,
                d.dataArquivado as dataArquivado,
                count(p.id) as qtdViagens
            FROM
                DestinoJpa d
            JOIN
                Passageiro p ON d.id = p.destinoId
            WHERE
                d.id = :id
            GROUP BY d.id""")
    Optional<DestinoProjecoesArquivado> buscarPorIdArquivado(@Param("id") UUID id);

    boolean existsByCidadeIdAndNome(@Param("idCidade") UUID idCidade, @Param("nome") String nome);

    boolean existsByCidadeIdAndNomeAndId(@Param("cidadeId") UUID cidadeId, @Param("nome") String nome,
            @Param("id") UUID id);
}
