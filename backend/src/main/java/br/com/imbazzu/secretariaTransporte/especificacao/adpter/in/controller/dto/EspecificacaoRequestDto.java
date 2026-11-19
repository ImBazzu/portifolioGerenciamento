package br.com.imbazzu.secretariaTransporte.especificacao.adpter.in.controller.dto;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EspecificacaoRequestDto(
        @NotBlank @Size(min = 3, max = 100) String nome,
        @NotNull EnumPrioridade prioridade,
        @Size(max = 249) String observacao) {
}
