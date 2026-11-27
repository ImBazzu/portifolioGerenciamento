package br.com.imbazzu.secretariaTransporte.veiculo.application.command.confirmarSaida;

import java.util.UUID;

public record AgendamentoCommandConfirmarSaida(UUID idVeiculo, UUID autorId) {
}
