package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto;

import java.util.UUID;

public record PessoaResponseDto(UUID id,
                                String nome,
                                String cpf,
                                UUID idCondicao,
                                String telefone,
                                String endereco,
                                String observacao) {
}
