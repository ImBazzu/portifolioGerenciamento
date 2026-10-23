package br.com.imbazzu.secretariaTransporte.lista.application.model;

import java.time.Duration;
import java.util.UUID;

public record RegraSeparacaoInput(UUID idTipoVeiculo, Duration tempoTolerancia) {
}
