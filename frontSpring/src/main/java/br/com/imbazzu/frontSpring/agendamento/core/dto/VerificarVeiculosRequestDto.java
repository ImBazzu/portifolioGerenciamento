package br.com.imbazzu.frontSpring.agendamento.core.dto;

import java.time.LocalTime;
import java.util.UUID;

public record VerificarVeiculosRequestDto(
        UUID idPessoa,
        UUID idLista,
        UUID idDestino,
        LocalTime horarioChegada,
        int acompanhantes) {
}
