package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.in.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * VeiculoRequestDto
 */
public record TipoVeiculoRequestDto(
                @Size(min = 3, max = 20, message = "Nome deve possuir entre 3 a 20 caracteres") String nome,
                @Min(0) @Max(100) int capacidade) {

}
