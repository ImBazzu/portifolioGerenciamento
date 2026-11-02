package br.com.imbazzu.secretariaTransporte.agendamento.application.command.atribuirVeiculo;

import java.util.UUID;

public record AgendamentoCommandAtribuirVeiculo(UUID idAgendamento, UUID idVeiculo) {
}
