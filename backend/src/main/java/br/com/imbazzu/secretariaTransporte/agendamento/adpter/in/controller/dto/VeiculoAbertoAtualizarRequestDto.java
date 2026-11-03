package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.Size;

public record VeiculoAbertoAtualizarRequestDto(
        @Size(max = 80) String identificacao,
        LocalTime horarioSaida) {
}
