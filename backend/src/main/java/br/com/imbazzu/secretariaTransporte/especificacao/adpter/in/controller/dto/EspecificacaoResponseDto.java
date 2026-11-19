package br.com.imbazzu.secretariaTransporte.especificacao.adpter.in.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;

public record EspecificacaoResponseDto(
        UUID id,
        String nome,
        EnumPrioridade prioridade,
        String observacao,
        LocalDate dataArquivado) {
}
