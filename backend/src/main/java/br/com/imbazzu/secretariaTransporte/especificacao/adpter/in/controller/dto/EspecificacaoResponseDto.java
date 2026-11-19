package br.com.imbazzu.secretariaTransporte.especificacao.adpter.in.controller.dto;

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
