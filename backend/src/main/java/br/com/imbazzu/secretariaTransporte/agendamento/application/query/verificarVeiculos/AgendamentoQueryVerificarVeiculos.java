package br.com.imbazzu.secretariaTransporte.agendamento.application.query.verificarVeiculos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoQueryVerificarVeiculos(
        LocalDate data,
        UUID idPessoa,
        UUID idLista,
        UUID idDestino,
        LocalTime horarioChegada,
        int acompanhantes) {
}
