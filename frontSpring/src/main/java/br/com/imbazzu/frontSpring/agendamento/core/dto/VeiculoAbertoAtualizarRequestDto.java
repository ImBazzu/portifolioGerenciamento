package br.com.imbazzu.frontSpring.agendamento.core.dto;

import java.time.LocalTime;

public record VeiculoAbertoAtualizarRequestDto(String identificacao, LocalTime horarioSaida) {
}
