package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model;

import java.time.LocalDate;
import java.util.UUID;

public record TipoVeiculoArquivadoOutput(
        UUID id,
        String nome,
        Integer capacidade,
        LocalDate dataArquivado) {

}
