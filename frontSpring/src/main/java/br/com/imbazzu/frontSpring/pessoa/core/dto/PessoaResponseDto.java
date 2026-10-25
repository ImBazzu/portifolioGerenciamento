package br.com.imbazzu.frontSpring.pessoa.core.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PessoaResponseDto(
        UUID id,
        String nome,
        String observacao,
        PessoaPrioridade prioridade,
        LocalDate dataArquivado,
        String endereco,
        String cpf,
        String telefone) {
}
