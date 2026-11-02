package br.com.imbazzu.secretariaTransporte.agendamento.application.query.model;

import java.time.LocalDate;
import java.util.List;

public record AgendaDia(
        LocalDate data,
        List<VeiculoAgenda> veiculos,
        List<AgendamentoAgenda> passageirosPendentes) {
}
