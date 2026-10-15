package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.entidadeJpa.TipoVeiculoJpa;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.projecoes.TipoVeiculoArquivadoProjecao;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.projecoes.TipoVeiculoAtivoProjecao;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.projecoes.TipoVeiculoHistoricoProjecao;

public interface TipoVeiculoQueryRepository extends JpaRepository<TipoVeiculoJpa, UUID> {

        @Query("""
                        SELECT
                                v.id as id,
                                v.nome as nome,
                                v.capacidade as capacidade
                        FROM TipoVeiculoJpa v
                        WHERE
                                v.nome LIKE CONCAT('%',:nome,'%')
                                AND v.dataArquivado IS NULL
                        ORDER BY v.nome ASC""")
        Page<TipoVeiculoAtivoProjecao> listarAtivo(
                        @Param("nome") String nome,
                        Pageable pageable);

        @Query("""
                        SELECT
                                v.id as id,
                                v.nome as nome,
                                v.capacidade as capacidade,
                                v.dataArquivado as dataArquivado
                        FROM TipoVeiculoJpa v
                        WHERE
                                v.nome LIKE CONCAT('%',:nome,'%')
                                AND v.dataArquivado IS NOT NULL""")
        Page<TipoVeiculoArquivadoProjecao> listarArquivado(
                        @Param("nome") String nome,
                        Pageable pageable);

        @Query("""
                        SELECT
                                v.id as id,
                                v.nome as nome,
                                v.capacidade as capacidade
                        FROM TipoVeiculoJpa v
                        WHERE
                                v.id=:id""")
        Optional<TipoVeiculoAtivoProjecao> buscarAtivoPorId(@Param("id") UUID id);

        @Query("""
                        SELECT
                                v.id as id,
                                v.nome as nome,
                                v.capacidade as capacidade,
                                v.dataArquivado as dataArquivado
                        FROM TipoVeiculoJpa v
                        WHERE
                        v.id = :id""")
        Optional<TipoVeiculoArquivadoProjecao> buscarArquivadoPorId(@Param("id") UUID id);

        @Query(value = """
                        SELECT
                                tp.id as id,
                                v.dataSaida AS data,
                                COUNT(DISTINCT v.id) AS qtdVeiculo,
                                SUM(v.qtdPassageiros) AS totalPassageiros,
                                'Aracaju/Itabaina' AS destinos
                        FROM
                                VeiculoJpa v
                        JOIN
                                TipoVeiculoJpa tp ON v.idTipoVeiculo = tp.id
                        WHERE
                                v.id = :idTipoVeiculo
                        GROUP BY
                                v.dataSaida""", countQuery = """
                        SELECT
                                COUNT(DISTINCT v.dataSaida)
                        FROM
                                VeiculoJpa v
                        JOIN
                        TipoVeiculoJpa tp ON v.id = tp.id
                        WHERE
                                tp.id = :id
                        """)
        Page<TipoVeiculoHistoricoProjecao> buscarHistorico(@Param("idTipoVeiculo") UUID id, Pageable pageable);
}
