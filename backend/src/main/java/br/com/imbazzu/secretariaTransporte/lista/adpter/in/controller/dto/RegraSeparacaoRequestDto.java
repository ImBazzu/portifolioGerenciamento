package br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RegraSeparacaoRequestDto(
        @NotNull UUID idTipoVeiculo,
        @Min(0) long tempoToleranciaSegundos,
        @Min(0) @Max(999999999) int tempoToleranciaNanos) {
}
