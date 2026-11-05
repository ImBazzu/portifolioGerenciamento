package br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.entidadeJpa.VeiculoAgendaJpa;
import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio.projecoes.VeiculoAgendaProjecao;

public interface VeiculoAgendaQueryRepository extends Repository<VeiculoAgendaJpa, UUID> {

    @Query("""
            SELECT v.id AS id, v.data AS data, v.idTipoVeiculo AS idTipoVeiculo,
                   v.nomeTipoVeiculo AS nomeTipoVeiculo, v.capacidade AS capacidade,
                   v.identificacao AS identificacao, v.horarioSaida AS horarioSaida,
                   v.horarioSaidaCalculado AS horarioSaidaCalculado
            FROM VeiculoAgendaJpa v
            WHERE v.id = :id
            """)
    Optional<VeiculoAgendaProjecao> buscarProjecaoPorId(@Param("id") UUID id);

    @Query("""
            SELECT v.id AS id, v.data AS data, v.idTipoVeiculo AS idTipoVeiculo,
                   v.nomeTipoVeiculo AS nomeTipoVeiculo, v.capacidade AS capacidade,
                   v.identificacao AS identificacao, v.horarioSaida AS horarioSaida,
                   v.horarioSaidaCalculado AS horarioSaidaCalculado
            FROM VeiculoAgendaJpa v
            WHERE v.data = :data
            ORDER BY v.id ASC
            """)
    List<VeiculoAgendaProjecao> listarProjecoesPorData(@Param("data") LocalDate data);
}
