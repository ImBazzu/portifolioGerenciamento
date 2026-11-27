package br.com.imbazzu.secretariaTransporte.relatorio.adpter.out;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.imbazzu.secretariaTransporte.relatorio.adpter.in.controller.RelatorioQueryRepository;
import br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto.EstatisticasAgendamentosDTO;
import br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto.EstatisticasOperacionaisDTO;
import br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto.EstatisticasPessoasDTO;
import br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto.EstatisticasVeiculosDTO;
import br.com.imbazzu.secretariaTransporte.relatorio.core.domain.FiltroRelatorio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@Repository
public class RelatorioJpaRepository implements RelatorioQueryRepository {
    private final EntityManager entityManager;

    public RelatorioJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private Query consulta(String sql, FiltroRelatorio filtro, String colunaData) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        if (filtro.getDataInicio() != null) where.append(" AND ").append(colunaData).append(" >= :inicio");
        if (filtro.getDataFim() != null) where.append(" AND ").append(colunaData).append(" <= :fim");
        Query query = entityManager.createNativeQuery(sql.replace("/*filtro*/", where.toString()));
        if (filtro.getDataInicio() != null) query.setParameter("inicio", filtro.getDataInicio());
        if (filtro.getDataFim() != null) query.setParameter("fim", filtro.getDataFim());
        return query;
    }

    private long numero(Object value) {
        return ((Number) value).longValue();
    }

    @Override
    public EstatisticasAgendamentosDTO obterEstatisticasAgendamentos(FiltroRelatorio filtro) {
        var dto = new EstatisticasAgendamentosDTO();
        dto.setTotalAgendamentos(numero(consulta("SELECT COUNT(*) FROM agendamentos a /*filtro*/", filtro, "a.data").getSingleResult()));
        dto.setAgendamentosCancelados(numero(consulta(
                "SELECT COUNT(*) FROM agendamentos a /*filtro*/ AND a.cancelado_em IS NOT NULL",
                filtro, "a.data").getSingleResult()));
        List<EstatisticasAgendamentosDTO.AgendamentoPorDataDTO> porData = new ArrayList<>();
        for (Object value : consulta(
                "SELECT a.data, COUNT(*), SUM(CASE WHEN a.cancelado_em IS NOT NULL THEN 1 ELSE 0 END) "
                        + "FROM agendamentos a /*filtro*/ GROUP BY a.data ORDER BY a.data",
                filtro, "a.data").getResultList()) {
            Object[] row = (Object[]) value;
            porData.add(new EstatisticasAgendamentosDTO.AgendamentoPorDataDTO(
                    LocalDate.parse(row[0].toString()), numero(row[1]), null, numero(row[2])));
        }
        dto.setAgendamentosPorData(porData);
        List<EstatisticasAgendamentosDTO.AgendamentoPorDestinoDTO> porDestino = new ArrayList<>();
        for (Object value : consulta(
                "SELECT d.nome, COUNT(*) FROM agendamentos a JOIN destinos d ON d.id = a.destino_id "
                        + "/*filtro*/ AND a.cancelado_em IS NULL GROUP BY d.id, d.nome "
                        + "ORDER BY COUNT(*) DESC, d.nome",
                filtro, "a.data").getResultList()) {
            Object[] row = (Object[]) value;
            porDestino.add(new EstatisticasAgendamentosDTO.AgendamentoPorDestinoDTO((String) row[0], numero(row[1])));
        }
        dto.setAgendamentosPorDestino(porDestino);
        return dto;
    }

    @Override
    public EstatisticasPessoasDTO obterEstatisticasPessoas(FiltroRelatorio filtro) {
        var dto = new EstatisticasPessoasDTO();
        Object[] totals = (Object[]) entityManager.createNativeQuery("SELECT COUNT(*), COUNT(data_arquivado) FROM pessoas").getSingleResult();
        long total = numero(totals[0]);
        long arquivadas = numero(totals[1]);
        dto.setTotalPessoas(total);
        dto.setPessoasAtivas(total - arquivadas);
        dto.setPessoasArquivadas(arquivadas);
        List<EstatisticasPessoasDTO.PessoaPorEspecificacaoDTO> porEspecificacao = new ArrayList<>();
        for (Object value : entityManager.createNativeQuery("SELECT e.nome, COUNT(*) FROM pessoas p JOIN especificacoes e ON e.id = p.especificacao_id GROUP BY e.id, e.nome ORDER BY COUNT(*) DESC, e.nome").getResultList()) {
            Object[] row = (Object[]) value;
            porEspecificacao.add(new EstatisticasPessoasDTO.PessoaPorEspecificacaoDTO((String) row[0], numero(row[1])));
        }
        dto.setPessoasPorEspecificacao(porEspecificacao);
        List<EstatisticasPessoasDTO.PessoaMaisFrequenteDTO> frequentes = new ArrayList<>();
        for (Object value : consulta(
                "SELECT p.nome, COUNT(*), e.nome FROM agendamentos a "
                        + "JOIN pessoas p ON p.id = a.pessoa_id "
                        + "JOIN especificacoes e ON e.id = p.especificacao_id "
                        + "/*filtro*/ AND a.cancelado_em IS NULL "
                        + "GROUP BY p.id, p.nome, e.nome ORDER BY COUNT(*) DESC, p.nome LIMIT 10",
                filtro, "a.data").getResultList()) {
            Object[] row = (Object[]) value;
            frequentes.add(new EstatisticasPessoasDTO.PessoaMaisFrequenteDTO((String) row[0], numero(row[1]), (String) row[2]));
        }
        dto.setPessoasMaisFrequentes(frequentes);
        return dto;
    }

    @Override
    public EstatisticasVeiculosDTO obterEstatisticasVeiculos(FiltroRelatorio filtro) {
        var dto = new EstatisticasVeiculosDTO();
        Object[] totals = (Object[]) entityManager.createNativeQuery("SELECT COUNT(*), COUNT(data_arquivado) FROM tipo_veiculos").getSingleResult();
        long total = numero(totals[0]);
        long arquivados = numero(totals[1]);
        dto.setTotalVeiculos(total);
        dto.setVeiculosAtivos(total - arquivados);
        dto.setVeiculosArquivados(arquivados);
        return dto;
    }

    @Override
    public EstatisticasOperacionaisDTO obterEstatisticasOperacionais(FiltroRelatorio filtro) {
        var dto = new EstatisticasOperacionaisDTO();
        Object[] totals = (Object[]) consulta(
                "SELECT COUNT(*), "
                        + "SUM(CASE WHEN a.cancelado_em IS NULL AND a.veiculo_agendamento_id IS NOT NULL THEN 1 ELSE 0 END), "
                        + "SUM(CASE WHEN a.cancelado_em IS NULL AND a.veiculo_agendamento_id IS NULL THEN 1 ELSE 0 END), "
                        + "SUM(CASE WHEN a.cancelado_em IS NOT NULL THEN 1 ELSE 0 END) "
                        + "FROM agendamentos a /*filtro*/",
                filtro, "a.data").getSingleResult();
        long total = numero(totals[0]);
        long alocados = numero(totals[1]);
        long pendentes = numero(totals[2]);
        long cancelados = numero(totals[3]);
        dto.setTotalAgendamentos(total);
        dto.setAgendamentosAlocados(alocados);
        dto.setAgendamentosSemVeiculo(pendentes);
        dto.setAgendamentosPendentes(pendentes);
        dto.setAgendamentosCancelados(cancelados);
        return dto;
    }
}
