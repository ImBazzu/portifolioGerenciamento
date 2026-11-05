package br.com.imbazzu.secretariaTransporte.veiculo.application.command.atualizarVeiculo;

import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoCommandAtualizarVeiculo(
        UUID idVeiculo,
        String identificacao,
        LocalTime horarioSaida) {
}
