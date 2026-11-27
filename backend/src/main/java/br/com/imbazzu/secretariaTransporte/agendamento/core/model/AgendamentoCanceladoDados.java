package br.com.imbazzu.secretariaTransporte.agendamento.core.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoCanceladoDados(
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
