package br.com.imbazzu.frontSpring.agendamento.core.dto;

import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoRequestDto(
        UUID idPessoa,
        UUID idLista,
        UUID idTipoVeiculo,
        UUID idDestino,
        LocalTime horarioChegada,
        boolean buscarNaResidencia,
        int acompanhantes,
        UUID idVeiculo,
        boolean informarHorarioSaida) {

    public AgendamentoRequestDto(
            UUID idPessoa, UUID idLista, UUID idTipoVeiculo, UUID idDestino,
            LocalTime horarioChegada, boolean buscarNaResidencia, int acompanhantes, UUID idVeiculo) {
        this(idPessoa, idLista, idTipoVeiculo, idDestino, horarioChegada,
                buscarNaResidencia, acompanhantes, idVeiculo, false);
    }
}
