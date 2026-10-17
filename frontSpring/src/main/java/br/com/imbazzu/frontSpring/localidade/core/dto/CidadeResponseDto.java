package br.com.imbazzu.frontSpring.localidade.core.dto;

import java.util.UUID;

public record CidadeResponseDto(
        UUID id,
        String nome,
        String estado,
        String tempoViagem,
        Integer totalDestinos) {
}
