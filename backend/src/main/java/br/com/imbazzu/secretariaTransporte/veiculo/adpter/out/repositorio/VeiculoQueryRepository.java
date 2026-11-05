package br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.entidadeJpa.VeiculoJpa;
import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio.projecoes.VeiculoHistoricoCidadeProjecao;
import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio.projecoes.VeiculoHistoricoResumoProjecao;

public interface VeiculoQueryRepository extends Repository<VeiculoJpa, UUID> {

    @Query(value = """
            SELECT
                v.dataSaida AS data,
                COUNT(v.id) AS qtdVeiculo,
                SUM(v.qtdPassageiros) AS totalPassageiros
            FROM VeiculoJpa v
            WHERE v.idTipoVeiculo = :idTipoVeiculo
            GROUP BY v.dataSaida
            ORDER BY v.dataSaida DESC
            """,
            countQuery = """
            SELECT COUNT(DISTINCT v.dataSaida)
            FROM VeiculoJpa v
            WHERE v.idTipoVeiculo = :idTipoVeiculo
            """)
    Page<VeiculoHistoricoResumoProjecao> buscarResumo(
            @Param("idTipoVeiculo") UUID idTipoVeiculo,
            Pageable pageable);

    @Query("""
            SELECT DISTINCT
                v.dataSaida AS data,
                c.nome AS cidade
            FROM VeiculoJpa v
            JOIN PassageiroJpa p ON p.veiculoId = v.id
            JOIN DestinoJpa d ON d.id = p.destinoId
            JOIN CidadeJpa c ON c.id = d.cidadeId
            WHERE v.idTipoVeiculo = :idTipoVeiculo
                AND v.dataSaida IN :datas
            ORDER BY c.nome ASC
            """)
    List<VeiculoHistoricoCidadeProjecao> buscarDestinosPorDatas(
            @Param("idTipoVeiculo") UUID idTipoVeiculo,
            @Param("datas") Collection<LocalDate> datas);
}
