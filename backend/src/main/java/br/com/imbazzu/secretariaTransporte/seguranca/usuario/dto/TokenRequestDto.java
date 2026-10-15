package br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRequestDto(@NotBlank String refreshToken) {
}
