package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto;

import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AgendamentoRequestDto(
        @NotNull UUID idPessoa,
        @NotNull UUID idLista,
        @NotNull UUID idRegraCadastro,
        @NotNull UUID idTipoVeiculo,
        @NotNull UUID idDestino,
        @NotNull LocalTime horarioChegada,
        boolean buscarNaResidencia,
        @Min(0) int acompanhantes) {
}
