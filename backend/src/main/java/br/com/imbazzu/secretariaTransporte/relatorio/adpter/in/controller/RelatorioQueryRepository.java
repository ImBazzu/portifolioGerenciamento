package br.com.imbazzu.secretariaTransporte.relatorio.adpter.in.controller;

import br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto.*;
import br.com.imbazzu.secretariaTransporte.relatorio.core.domain.FiltroRelatorio;

public interface RelatorioQueryRepository {
    EstatisticasAgendamentosDTO obterEstatisticasAgendamentos(FiltroRelatorio filtro);
    EstatisticasPessoasDTO obterEstatisticasPessoas(FiltroRelatorio filtro);
    EstatisticasVeiculosDTO obterEstatisticasVeiculos(FiltroRelatorio filtro);
    EstatisticasOperacionaisDTO obterEstatisticasOperacionais(FiltroRelatorio filtro);
}
