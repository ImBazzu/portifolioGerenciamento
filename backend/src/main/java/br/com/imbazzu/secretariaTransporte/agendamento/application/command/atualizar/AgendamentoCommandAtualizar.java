package br.com.imbazzu.secretariaTransporte.agendamento.application.command.atualizar;

import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoCommandAtualizar(
        UUID id,
        UUID idTipoVeiculo,
        UUID idDestino,
        LocalTime horarioChegada,
        boolean buscarNaResidencia,
        int acompanhantes,
        UUID idVeiculo) {
}
