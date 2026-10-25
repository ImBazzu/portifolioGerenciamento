package br.com.imbazzu.frontSpring.pessoa.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PessoaRequestDto(
        @NotBlank @Size(min = 3, max = 59) String nome,
        @Size(max = 249) String observacao,
        @NotNull PessoaPrioridade prioridade,
        @NotBlank @Size(min = 10, max = 249) String endereco,
        @NotBlank String cpf,
        @NotBlank String telefone) {
}
