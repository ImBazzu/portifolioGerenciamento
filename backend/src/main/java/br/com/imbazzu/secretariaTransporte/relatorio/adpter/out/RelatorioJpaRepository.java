package br.com.imbazzu.secretariaTransporte.relatorio.adpter.out;

import br.com.imbazzu.secretariaTransporte.relatorio.adpter.in.controller.RelatorioQueryRepository;
import br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto.*;
import br.com.imbazzu.secretariaTransporte.relatorio.core.domain.FiltroRelatorio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RelatorioJpaRepository implements RelatorioQueryRepository {

    private final EntityManager entityManager;

    public RelatorioJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public EstatisticasAgendamentosDTO obterEstatisticasAgendamentos(FiltroRelatorio filtro) {
        EstatisticasAgendamentosDTO dto = new EstatisticasAgendamentosDTO();
        
        // Total de agendamentos
        String sqlTotal = "SELECT COUNT(a.id) FROM agendamento a WHERE a.data_arquivado IS NULL";
        String sqlRealizados = "SELECT COUNT(a.id) FROM agendamento a WHERE a.data_arquivado IS NULL AND a.status = 'REALIZADO'";
        String sqlCancelados = "SELECT COUNT(a.id) FROM agendamento a WHERE a.data_arquivado IS NULL AND a.status = 'CANCELADO'";
        String sqlPendentes = "SELECT COUNT(a.id) FROM agendamento a WHERE a.data_arquivado IS NULL AND a.status = 'PENDENTE'";

        Long total = ((Number) entityManager.createNativeQuery(sqlTotal).getSingleResult()).longValue();
        Long realizados = ((Number) entityManager.createNativeQuery(sqlRealizados).getSingleResult()).longValue();
        Long cancelados = ((Number) entityManager.createNativeQuery(sqlCancelados).getSingleResult()).longValue();
        Long pendentes = ((Number) entityManager.createNativeQuery(sqlPendentes).getSingleResult()).longValue();

        dto.setTotalAgendamentos(total);
        dto.setAgendamentosRealizados(realizados);
        dto.setAgendamentosCancelados(cancelados);
        dto.setAgendamentosPendentes(pendentes);
        
        Double taxaRealizacao = total > 0 ? (realizados.doubleValue() / total) * 100 : 0.0;
        dto.setTaxaRealizacao(taxaRealizacao);

        return dto;
    }

    @Override
    public EstatisticasPessoasDTO obterEstatisticasPessoas(FiltroRelatorio filtro) {
        EstatisticasPessoasDTO dto = new EstatisticasPessoasDTO();
        
        String sqlTotal = "SELECT COUNT(p.id) FROM pessoa p WHERE p.data_arquivado IS NULL";
        String sqlArquivadas = "SELECT COUNT(p.id) FROM pessoa p WHERE p.data_arquivado IS NOT NULL";

        Long total = ((Number) entityManager.createNativeQuery(sqlTotal).getSingleResult()).longValue();
        Long arquivadas = ((Number) entityManager.createNativeQuery(sqlArquivadas).getSingleResult()).longValue();
        Long ativas = total - arquivadas;

        dto.setTotalPessoas(total);
        dto.setPessoasAtivas(ativas);
        dto.setPessoasArquivadas(arquivadas);

        return dto;
    }

    @Override
    public EstatisticasVeiculosDTO obterEstatisticasVeiculos(FiltroRelatorio filtro) {
        EstatisticasVeiculosDTO dto = new EstatisticasVeiculosDTO();
        
        String sqlTotal = "SELECT COUNT(v.id) FROM veiculo v WHERE v.data_arquivado IS NULL";
        String sqlArquivados = "SELECT COUNT(v.id) FROM veiculo v WHERE v.data_arquivado IS NOT NULL";

        Long total = ((Number) entityManager.createNativeQuery(sqlTotal).getSingleResult()).longValue();
        Long arquivados = ((Number) entityManager.createNativeQuery(sqlArquivados).getSingleResult()).longValue();
        Long ativos = total - arquivados;

        dto.setTotalVeiculos(total);
        dto.setVeiculosAtivos(ativos);
        dto.setVeiculosArquivados(arquivados);

        return dto;
    }

    @Override
    public EstatisticasOperacionaisDTO obterEstatisticasOperacionais(FiltroRelatorio filtro) {
        EstatisticasOperacionaisDTO dto = new EstatisticasOperacionaisDTO();
        
        String sqlTotal = "SELECT COUNT(a.id) FROM agendamento a WHERE a.data_arquivado IS NULL";
        String sqlRealizados = "SELECT COUNT(a.id) FROM agendamento a WHERE a.data_arquivado IS NULL AND a.status = 'REALIZADO'";
        String sqlCancelados = "SELECT COUNT(a.id) FROM agendamento a WHERE a.data_arquivado IS NULL AND a.status = 'CANCELADO'";
        String sqlPendentes = "SELECT COUNT(a.id) FROM agendamento a WHERE a.data_arquivado IS NULL AND a.status = 'PENDENTE'";

        Long total = ((Number) entityManager.createNativeQuery(sqlTotal).getSingleResult()).longValue();
        Long realizados = ((Number) entityManager.createNativeQuery(sqlRealizados).getSingleResult()).longValue();
        Long cancelados = ((Number) entityManager.createNativeQuery(sqlCancelados).getSingleResult()).longValue();
        Long pendentes = ((Number) entityManager.createNativeQuery(sqlPendentes).getSingleResult()).longValue();

        Double taxaRealizacao = total > 0 ? (realizados.doubleValue() / total) * 100 : 0.0;
        Double taxaCancelamento = total > 0 ? (cancelados.doubleValue() / total) * 100 : 0.0;
        Double eficiencia = taxaRealizacao;

        dto.setEficienciaOperacional(eficiencia);
        dto.setTaxaRealizacaoGeral(taxaRealizacao);
        dto.setTaxaCancelamento(taxaCancelamento);
        dto.setAgendamentosPendentes(pendentes);
        dto.setAgendamentosCancelados(cancelados);

        return dto;
    }
}
