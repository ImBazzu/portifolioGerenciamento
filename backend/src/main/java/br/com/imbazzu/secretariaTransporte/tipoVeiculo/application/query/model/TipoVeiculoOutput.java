package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model;

import java.util.UUID;

public record TipoVeiculoOutput(
        UUID id,
        String nome,
        Integer capacidadde) {

}
