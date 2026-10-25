package br.com.imbazzu.secretariaTransporte.agendamento.core.model;

import java.time.Duration;
import java.util.UUID;

public record DestinoAgendaDados(UUID idCidade, Duration tempoViagem, boolean ativo) {
}
