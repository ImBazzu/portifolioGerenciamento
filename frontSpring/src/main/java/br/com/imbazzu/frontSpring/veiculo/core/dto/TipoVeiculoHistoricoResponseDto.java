package br.com.imbazzu.frontSpring.veiculo.core.dto;

import java.time.LocalDate;
import java.util.UUID;

public record TipoVeiculoHistoricoResponseDto(
        UUID id,
        LocalDate data,
        Integer quantidadeVeiculos,
        String destinos,
        Integer totalPassageiro) {

}