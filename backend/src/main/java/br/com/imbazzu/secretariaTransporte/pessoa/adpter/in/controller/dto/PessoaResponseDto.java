package br.com.imbazzu.secretariaTransporte.pessoa.adpter.in.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;

public record PessoaResponseDto(
        UUID id,
        String nome,
        String observacao,
        UUID especificacaoId,
        String especificacaoNome,
        EnumPrioridade especificacaoPrioridade,
        boolean buscarNaResidencia,
        int quantidadeAcompanhantes,
        LocalDate dataArquivado,
        String endereco,
        String cpf,
        String telefone) {
}
