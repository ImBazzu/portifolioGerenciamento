package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record PessoaCondicaoRequestDto(
        @NotBlank(message = "Informar nome da justificativa")
        String nome) {
}
