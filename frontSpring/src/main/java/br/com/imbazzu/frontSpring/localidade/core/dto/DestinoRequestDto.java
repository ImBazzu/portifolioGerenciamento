package br.com.imbazzu.frontSpring.localidade.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DestinoRequestDto(
        @NotNull(message = "Cidade é obrigatória") UUID idCidade,
        @NotBlank(message = "Nome do destino é obrigatório") String nome) {
}
