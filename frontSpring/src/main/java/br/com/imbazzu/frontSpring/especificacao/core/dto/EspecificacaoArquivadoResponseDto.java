package br.com.imbazzu.frontSpring.especificacao.core.dto;

import java.time.LocalDate;
import java.util.UUID;
import br.com.imbazzu.frontSpring.pessoa.core.dto.EnumPrioridade;

public record EspecificacaoArquivadoResponseDto(
        UUID id,
        String nome,
        EnumPrioridade prioridade,
        String observacao,
        LocalDate dataArquivado) {
}
