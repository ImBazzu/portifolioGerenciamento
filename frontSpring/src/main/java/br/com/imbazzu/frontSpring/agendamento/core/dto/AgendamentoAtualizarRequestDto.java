package br.com.imbazzu.frontSpring.agendamento.core.dto;

import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoAtualizarRequestDto(
        UUID idTipoVeiculo,
        UUID idDestino,
        LocalTime horarioChegada,
        boolean buscarNaResidencia,
        int acompanhantes,
        UUID idVeiculo) {
}
