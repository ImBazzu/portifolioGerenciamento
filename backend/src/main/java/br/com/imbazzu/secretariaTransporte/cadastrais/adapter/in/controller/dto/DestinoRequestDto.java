package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DestinoRequestDto(
        @NotNull(message = "Id da cidade é obrigatório")
        UUID idCidade,
        @NotBlank(message = "Nome do destino é obrigatório")
        String nome) {
}
