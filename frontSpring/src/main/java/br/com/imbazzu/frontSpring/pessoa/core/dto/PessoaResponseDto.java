package br.com.imbazzu.frontSpring.pessoa.core.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PessoaResponseDto(
        UUID id,
        String nome,
        String observacao,
        UUID especificacaoId,
        String especificacaoNome,
        String especificacaoPrioridade,
        UUID prioridadeId,
        int prioridadeAcompanhantes,
        boolean buscarNaResidencia,
        int quantidadeAcompanhantes,
        boolean validada,
        LocalDate dataArquivado,
        String endereco,
        String cpf,
        String telefone,
        String autorNome) {
    public PessoaResponseDto(UUID id, String nome, String observacao, UUID especificacaoId,
            String especificacaoNome, String especificacaoPrioridade, UUID prioridadeId, int prioridadeAcompanhantes,
            boolean buscarNaResidencia, int quantidadeAcompanhantes, boolean validada, LocalDate dataArquivado,
            String endereco, String cpf, String telefone) {
        this(id, nome, observacao, especificacaoId, especificacaoNome, especificacaoPrioridade, prioridadeId,
                prioridadeAcompanhantes, buscarNaResidencia, quantidadeAcompanhantes, validada, dataArquivado,
                endereco, cpf, telefone, null);
    }
}
