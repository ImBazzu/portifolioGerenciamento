package br.com.imbazzu.secretariaTransporte.lista.adpter.model;

import java.time.Duration;
import java.util.UUID;

public record RegraSeparacaoInput(UUID idTipoVeiculo, Duration tempoTolerancia) {
}
