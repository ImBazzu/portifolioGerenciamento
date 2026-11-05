package br.com.imbazzu.secretariaTransporte.veiculo.application.query.model;

import java.time.LocalDate;
import java.util.UUID;

public record VeiculoHistoricoOutput(
        UUID id,
        LocalDate data,
        Integer quantidadeVeiculos,
        String destinos,
        Integer totalPassageiro) {
}
