package br.com.imbazzu.frontSpring.autenticacao.core.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Login obrigatório")
        String login,
        @NotBlank(message = "Senha Obrigatório")
        String senha) {

}
