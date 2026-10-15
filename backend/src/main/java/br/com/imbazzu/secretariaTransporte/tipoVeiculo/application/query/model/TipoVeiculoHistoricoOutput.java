package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * VeiculoHistoricoOutPut
 */
public record TipoVeiculoHistoricoOutput(
                UUID id,
                LocalDate data,
                Integer quantidadeVeiculos,
                String destinos,
                Integer totalPassageiro) {

}
