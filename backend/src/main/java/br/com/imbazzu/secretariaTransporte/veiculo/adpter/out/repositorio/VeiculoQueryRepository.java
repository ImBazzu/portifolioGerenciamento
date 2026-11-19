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

import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.entidadeJpa.VeiculoAgendaJpa;
import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio.projecoes.VeiculoHistoricoCidadeProjecao;
import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio.projecoes.VeiculoHistoricoResumoProjecao;

public interface VeiculoQueryRepository extends Repository<VeiculoAgendaJpa, UUID> {

    @Query(value = """
            SELECT
                v.data AS data,
                COUNT(DISTINCT v.id) AS qtdVeiculo,
                COALESCE(SUM(p.acompanhantes + 1), 0) AS totalPassageiros
            FROM VeiculoAgendaJpa v
            LEFT JOIN AgendamentoJpa p ON p.idVeiculoAberto = v.id
            WHERE v.idTipoVeiculo = :idTipoVeiculo
            GROUP BY v.data
            ORDER BY v.data DESC
            """,
            countQuery = """
            SELECT COUNT(DISTINCT v.data)
            FROM VeiculoAgendaJpa v
            WHERE v.idTipoVeiculo = :idTipoVeiculo
            """)
    Page<VeiculoHistoricoResumoProjecao> buscarResumo(
            @Param("idTipoVeiculo") UUID idTipoVeiculo,
            Pageable pageable);

    @Query("""
            SELECT DISTINCT
                v.data AS data,
                c.nome AS cidade
            FROM VeiculoAgendaJpa v
            JOIN AgendamentoJpa p ON p.idVeiculoAberto = v.id
            JOIN DestinoJpa d ON d.id = p.idDestino
            JOIN CidadeJpa c ON c.id = d.cidadeId
            WHERE v.idTipoVeiculo = :idTipoVeiculo
                AND v.data IN :datas
            ORDER BY c.nome ASC
            """)
    List<VeiculoHistoricoCidadeProjecao> buscarDestinosPorDatas(
            @Param("idTipoVeiculo") UUID idTipoVeiculo,
            @Param("datas") Collection<LocalDate> datas);
}
