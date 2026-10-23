package br.com.imbazzu.frontSpring.lista.core.dto;

import java.util.UUID;

public record ListaResumoApiDto(
        UUID id,
        String nome,
        String descricao,
        long totalRegras,
        long totalCidades,
        boolean arquivado,
        String dataArquivado) {
}
