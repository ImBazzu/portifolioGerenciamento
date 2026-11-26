package br.com.imbazzu.secretariaTransporte.localidades.adapters.in.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CidadeRequestDto(
@NotBlank(message = "Nome do Cidade é obrigatório") String nome,

@NotNull(message = "Estado é obrigatório") String estado,

@NotNull(message = "Hora da viagem é obrigatório") @Min(value = 0, message =
"Hora de viagem inválido") @Max(value = 23, message = "A viagem deve durar menos de 24 horas") Integer horas,

@NotNull(message = "Minuto da viagem é obrigatório") @Min(value = 0, message
= "Minuto de viagem inválido") @Max(value = 59, message = "Minuto de viagem inválido") Integer minutos) {
}
