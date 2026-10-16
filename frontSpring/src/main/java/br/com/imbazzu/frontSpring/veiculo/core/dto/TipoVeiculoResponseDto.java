package br.com.imbazzu.frontSpring.veiculo.core.dto;

import java.util.UUID;

public record TipoVeiculoResponseDto(
        UUID id,
        String nome,
        int capacidade) {
}
