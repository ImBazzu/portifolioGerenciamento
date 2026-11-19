package br.com.imbazzu.secretariaTransporte.relatorio.adpter.in.controller;

import br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto.*;
import br.com.imbazzu.secretariaTransporte.relatorio.core.domain.FiltroRelatorio;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {

    private final RelatorioQueryRepository queryRepository;

    public RelatorioService(RelatorioQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    public EstatisticasAgendamentosDTO obterEstatisticasAgendamentos(FiltroRelatorio filtro) {
        return queryRepository.obterEstatisticasAgendamentos(filtro);
    }

    public EstatisticasPessoasDTO obterEstatisticasPessoas(FiltroRelatorio filtro) {
        return queryRepository.obterEstatisticasPessoas(filtro);
    }

    public EstatisticasVeiculosDTO obterEstatisticasVeiculos(FiltroRelatorio filtro) {
        return queryRepository.obterEstatisticasVeiculos(filtro);
    }

    public EstatisticasOperacionaisDTO obterEstatisticasOperacionais(FiltroRelatorio filtro) {
        return queryRepository.obterEstatisticasOperacionais(filtro);
    }
}
