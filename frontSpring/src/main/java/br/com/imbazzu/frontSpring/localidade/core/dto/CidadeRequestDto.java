package br.com.imbazzu.frontSpring.localidade.core.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CidadeRequestDto(
        @NotBlank(message = "Nome da cidade é obrigatório") String nome,
        @NotBlank(message = "Estado é obrigatório") String estado,
        @NotNull(message = "Hora da viagem é obrigatória") @Min(value = 0, message = "Hora da viagem inválida") Integer horas,
        @NotNull(message = "Minuto da viagem é obrigatório") @Min(value = 0, message = "Minuto da viagem inválido") Integer minutos) {
}
