package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.in.controller.dto;

import java.util.UUID;

public record TipoVeiculoResponseDto(
        UUID id,
        String nome,
        int capacidade) {
}
