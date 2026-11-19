package br.com.imbazzu.secretariaTransporte.pessoa.adpter.in.controller.dto;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PessoaDetalhesResponseDto(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("nome")
        String nome,
        @JsonProperty("observacao")
        String observacao,
        @JsonProperty("especificacaoId")
        UUID especificacaoId,
        @JsonProperty("especificacaoNome")
        String especificacaoNome,
        @JsonProperty("prioridadeNome")
        String prioridadeNome,
        @JsonProperty("prioridadeId")
        UUID prioridadeId,
        @JsonProperty("buscarNaResidenciaPrioridade")
        boolean buscarNaResidenciaPrioridade,
        @JsonProperty("quantidadeAcompanhantesPrioridade")
        int quantidadeAcompanhantesPrioridade,
        @JsonProperty("buscarNaResidencia")
        boolean buscarNaResidencia,
        @JsonProperty("quantidadeAcompanhantes")
        int quantidadeAcompanhantes,
        @JsonProperty("dataArquivado")
        String dataArquivado,
        @JsonProperty("endereco")
        String endereco,
        @JsonProperty("cpf")
        String cpf,
        @JsonProperty("telefone")
        String telefone) {
}
