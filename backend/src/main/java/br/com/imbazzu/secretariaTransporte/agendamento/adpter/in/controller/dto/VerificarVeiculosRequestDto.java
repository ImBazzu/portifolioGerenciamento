package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto;

import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record VerificarVeiculosRequestDto(
        @NotNull UUID idPessoa,
        @NotNull UUID idLista,
        @NotNull UUID idDestino,
        @NotNull LocalTime horarioChegada,
        @Min(0) int acompanhantes,
        Boolean informarHorarioSaida) {

    public VerificarVeiculosRequestDto {
        informarHorarioSaida = Boolean.TRUE.equals(informarHorarioSaida);
    }

    public VerificarVeiculosRequestDto(
            UUID idPessoa, UUID idLista, UUID idDestino, LocalTime horarioChegada, int acompanhantes) {
        this(idPessoa, idLista, idDestino, horarioChegada, acompanhantes, false);
    }
}
