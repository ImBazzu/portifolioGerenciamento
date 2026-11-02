package br.com.imbazzu.secretariaTransporte.agendamento.application.query.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record VeiculoAgenda(
        UUID id,
        LocalDate data,
        UUID idTipoVeiculo,
        String nomeTipoVeiculo,
        int capacidade,
        String identificacao,
        LocalTime horarioSaida,
        int assentosOcupados,
        List<AgendamentoAgenda> passageiros) {
}
