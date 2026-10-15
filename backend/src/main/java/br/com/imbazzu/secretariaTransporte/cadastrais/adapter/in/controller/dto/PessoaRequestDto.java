package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PessoaRequestDto(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotBlank(message = "CPF é obrigatório") String cpf,
        @NotNull(message = "Id da condição é obrigatorio")
        UUID idCondicao,
        @NotBlank(message = "Telefone é obrigatório") String telefone,
        String endereco,
        String observacao) {
}