package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto;

import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AbrirVeiculoRequestDto(
        @NotNull UUID idTipoVeiculo,
        @Size(max = 80) String identificacao,
        LocalTime horarioSaida) {
}
