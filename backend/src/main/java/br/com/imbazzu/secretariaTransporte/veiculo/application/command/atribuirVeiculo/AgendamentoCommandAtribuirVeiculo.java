package br.com.imbazzu.secretariaTransporte.veiculo.application.command.atribuirVeiculo;

import java.util.UUID;

public record AgendamentoCommandAtribuirVeiculo(UUID idAgendamento, UUID idVeiculo) {
}
