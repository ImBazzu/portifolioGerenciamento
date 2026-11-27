package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoCanceladoResponseDto(
        UUID id,
        LocalDate data,
        UUID idPessoa,
        UUID idLista,
        UUID idTipoVeiculo,
        UUID idDestino,
        LocalTime horarioChegada,
        boolean buscarNaResidencia,
        int acompanhantes,
        String autorNome,
        LocalDateTime canceladoEm,
        String motivoCancelamento) {
}
