package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.editar;

import java.util.UUID;

public record TipoVeiculoCommandEditar(
                UUID id,
                String nome,
                Integer capacidade) {

}
