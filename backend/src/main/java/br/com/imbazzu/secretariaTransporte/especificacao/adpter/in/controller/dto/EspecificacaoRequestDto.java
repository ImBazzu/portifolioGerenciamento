package br.com.imbazzu.secretariaTransporte.especificacao.adpter.in.controller.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EspecificacaoRequestDto(
        @NotBlank @Size(min = 3, max = 100) String nome,
        @NotNull UUID prioridade,
        @Size(max = 249) String observacao) {
}
