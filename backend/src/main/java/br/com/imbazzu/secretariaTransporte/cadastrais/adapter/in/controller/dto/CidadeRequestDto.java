// package br.com.imbazzu.secretariaTransporte.veiculo.adpter.in.controller;

// import
// br.com.imbazzu.secretariaTransporte.cadastrais.core.model.CidadeEstadoEnum;
// import jakarta.validation.constraints.Min;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;

// public record CidadeRequestDto(
// @NotBlank(message = "Nome do Cidade é obrigatório") String nome,

// @NotNull(message = "Estado é obrigatório") CidadeEstadoEnum estado,

// @NotNull(message = "Hora da viagem é obrigatório") @Min(value = 0, message =
// "Hora de viagem inválido") long horas,

// @NotNull(message = "Minuto da viagem é obrigatório") @Min(value = 0, message
// = "Minuto de viagem inválido") long minutos) {
// }
