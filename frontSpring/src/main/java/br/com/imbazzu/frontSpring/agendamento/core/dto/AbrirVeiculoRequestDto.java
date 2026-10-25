package br.com.imbazzu.frontSpring.agendamento.core.dto;

import java.time.LocalTime;
import java.util.UUID;

public record AbrirVeiculoRequestDto(UUID idTipoVeiculo, String identificacao, LocalTime horarioSaida) {
}
