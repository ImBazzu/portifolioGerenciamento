package br.com.imbazzu.frontSpring.especificacao.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import br.com.imbazzu.frontSpring.pessoa.core.dto.EnumPrioridade;

public record EspecificacaoRequestDto(
        @NotBlank @Size(min = 3, max = 59) String nome,
        @NotNull EnumPrioridade prioridade,
        @Size(max = 249) String observacao) {
}
