package br.com.imbazzu.secretariaTransporte.agendamento.application.query.model;

import java.time.LocalDate;
import java.util.List;

import br.com.imbazzu.secretariaTransporte.agendamento.core.model.AgendamentoCanceladoDados;

public record AgendaDia(
        LocalDate data,
        List<VeiculoAgenda> veiculos,
        List<AgendamentoAgenda> passageirosPendentes,
        List<AgendamentoCanceladoDados> passageirosCancelados) {

    public AgendaDia(LocalDate data, List<VeiculoAgenda> veiculos, List<AgendamentoAgenda> passageirosPendentes) {
        this(data, veiculos, passageirosPendentes, List.of());
    }
}
