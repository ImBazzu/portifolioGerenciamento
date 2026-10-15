package br.com.imbazzu.frontspring.veiculo.core.dto;

import java.util.UUID;

public record TipoVeiculoResponseDto(
        UUID id,
        String nome,
        int capacidade) {
}
