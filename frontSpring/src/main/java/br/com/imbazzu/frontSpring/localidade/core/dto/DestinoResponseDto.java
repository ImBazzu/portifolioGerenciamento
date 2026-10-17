package br.com.imbazzu.frontSpring.localidade.core.dto;

import java.util.UUID;

public record DestinoResponseDto(
        UUID id,
        String nome,
        Integer qtdPassageiros) {
}
