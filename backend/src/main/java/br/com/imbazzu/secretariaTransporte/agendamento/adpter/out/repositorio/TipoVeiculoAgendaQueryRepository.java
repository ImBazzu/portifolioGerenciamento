package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.TipoVeiculoAgendaProjecao;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.entidadeJpa.TipoVeiculoJpa;

public interface TipoVeiculoAgendaQueryRepository extends Repository<TipoVeiculoJpa, UUID> {

    @Query("""
            SELECT t.id AS id, t.nome AS nome, t.capacidade AS capacidade,
                   CASE WHEN t.dataArquivado IS NULL THEN true ELSE false END AS ativo
            FROM TipoVeiculoJpa t
            WHERE t.id = :id
            """)
    Optional<TipoVeiculoAgendaProjecao> buscarProjecaoPorId(@Param("id") UUID id);
}
