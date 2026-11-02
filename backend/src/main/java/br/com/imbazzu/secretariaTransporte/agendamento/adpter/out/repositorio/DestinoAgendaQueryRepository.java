package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.DestinoAgendaProjecao;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.entiadeJpa.DestinoJpa;

public interface DestinoAgendaQueryRepository extends Repository<DestinoJpa, UUID> {

    @Query("""
            SELECT c.id AS idCidade, c.tempoViagem AS tempoViagem,
                   CASE WHEN d.dataArquivado IS NULL AND c.dataArquivado IS NULL
                        THEN true ELSE false END AS ativo
            FROM DestinoJpa d
            JOIN CidadeJpa c ON c.id = d.cidadeId
            WHERE d.id = :id
            """)
    Optional<DestinoAgendaProjecao> buscarProjecaoPorId(@Param("id") UUID id);
}
