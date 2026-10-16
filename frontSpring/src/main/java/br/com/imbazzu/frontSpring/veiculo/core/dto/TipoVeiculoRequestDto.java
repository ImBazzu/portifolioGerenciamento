package br.com.imbazzu.frontSpring.veiculo.core.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * VeiculoRequestDto
 */
public record TipoVeiculoRequestDto(
        @Size(min = 3, max = 20, message = "Nome deve possuir entre 3 a 20 caracteres") String nome,
        @Min(1) int capacidade) {

}
