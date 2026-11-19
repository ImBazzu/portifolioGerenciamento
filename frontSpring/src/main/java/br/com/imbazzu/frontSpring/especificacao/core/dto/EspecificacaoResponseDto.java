package br.com.imbazzu.frontSpring.especificacao.core.dto;

import java.time.LocalDate;
import java.util.UUID;

public record EspecificacaoResponseDto(
        UUID id,
        String nome,
        UUID prioridade,
        String prioridadeNome,
        String observacao,
        LocalDate dataArquivado) {
}
