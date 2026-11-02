package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.CidadeRegraAgendaProjecao;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.PrioridadeRegraAgendaProjecao;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.RegraCadastroAgendaProjecao;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.TipoRegraAgendaProjecao;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.ToleranciaRegraAgendaProjecao;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.entidadeJpa.TipoListaJpa;

public interface TipoListaAgendaQueryRepository extends Repository<TipoListaJpa, UUID> {

    boolean existsByIdAndDataArquivadoIsNull(UUID id);

    @Query("""
            SELECT r.id AS id, r.obrigacaoAnexo AS obrigacaoAnexo,
                   r.inicioHorario AS inicioHorario, r.finalHorario AS finalHorario
            FROM RegraCadastroJpa r
            WHERE r.lista.id = :idLista
            """)
    List<RegraCadastroAgendaProjecao> listarRegrasCadastro(@Param("idLista") UUID idLista);

    @Query("""
            SELECT r.id AS id, prioridade AS prioridade
            FROM RegraCadastroJpa r JOIN r.prioridades prioridade
            WHERE r.lista.id = :idLista
            """)
    List<PrioridadeRegraAgendaProjecao> listarPrioridadesCadastro(@Param("idLista") UUID idLista);

    @Query("""
            SELECT r.id AS id, idTipoVeiculo AS idTipoVeiculo
            FROM RegraCadastroJpa r JOIN r.idsVeiculos idTipoVeiculo
            WHERE r.lista.id = :idLista
            """)
    List<TipoRegraAgendaProjecao> listarTiposCadastro(@Param("idLista") UUID idLista);

    @Query("""
            SELECT r.id AS id, idCidade AS idCidade
            FROM RegraCadastroJpa r JOIN r.idsCidades idCidade
            WHERE r.lista.id = :idLista
            """)
    List<CidadeRegraAgendaProjecao> listarCidadesCadastro(@Param("idLista") UUID idLista);

    @Query("""
            SELECT r.tempoToleranciaSegundos AS segundos,
                   r.tempoToleranciaNanos AS nanos
            FROM RegraSeparacaoJpa r
            WHERE r.lista.id = :idLista AND r.tipoVeiculo.id = :idTipoVeiculo
            """)
    List<ToleranciaRegraAgendaProjecao> buscarToleranciasSeparacao(
            @Param("idLista") UUID idLista, @Param("idTipoVeiculo") UUID idTipoVeiculo);
}
