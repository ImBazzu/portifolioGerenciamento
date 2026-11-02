package br.com.imbazzu.secretariaTransporte.agendamento.application.command.abrirVeiculo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoCommandAbrirVeiculo(
        LocalDate data,
        UUID idTipoVeiculo,
        String identificacao,
        LocalTime horarioSaida) {
}
