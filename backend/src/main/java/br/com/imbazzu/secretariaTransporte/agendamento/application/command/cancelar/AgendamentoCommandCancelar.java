package br.com.imbazzu.secretariaTransporte.agendamento.application.command.cancelar;

import java.util.UUID;

public record AgendamentoCommandCancelar(UUID id, String motivo, UUID autorId) {
}
