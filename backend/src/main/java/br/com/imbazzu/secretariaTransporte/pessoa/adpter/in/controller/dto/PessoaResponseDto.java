package br.com.imbazzu.secretariaTransporte.pessoa.adpter.in.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PessoaResponseDto(
        UUID id,
        String nome,
        String observacao,
        UUID especificacaoId,
        String especificacaoNome,
        boolean buscarNaResidencia,
        int quantidadeAcompanhantes,
        LocalDate dataArquivado,
        String endereco,
        String cpf,
        String telefone) {
}
