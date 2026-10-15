package br.com.imbazzu.secretariaTransporte.localidades.adapters.in.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CidadeArquivadoResponseDto(
        UUID id,
        String nome,
        String estado,
        String tempoViagem,
        Integer totalDestinos,
        LocalDate dataArquivamento) {
}
