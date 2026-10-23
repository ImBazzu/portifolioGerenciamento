package br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

public record TipoListaResumoResponseDto(
        UUID id,
        String nome,
        String descricao,
        long totalRegras,
        long totalCidades,
        boolean arquivado,
        LocalDate dataArquivado) {
}
