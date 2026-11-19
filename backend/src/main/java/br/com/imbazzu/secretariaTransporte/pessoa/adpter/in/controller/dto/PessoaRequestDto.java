package br.com.imbazzu.secretariaTransporte.pessoa.adpter.in.controller.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PessoaRequestDto(
        @NotBlank @Size(min = 3, max = 59) String nome,
        @Size(max = 249) String observacao,
        @NotNull UUID especificacaoId,
        @Size(max = 249) String endereco,
        String cpf,
        @NotBlank String telefone) {
}
