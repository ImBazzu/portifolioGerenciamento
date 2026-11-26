package br.com.imbazzu.secretariaTransporte.agendamento.application.command.criar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoCommandCriar(
        LocalDate data,
        UUID idPessoa,
        UUID idLista,
        UUID idRegraCadastro,
        UUID idTipoVeiculo,
        UUID idDestino,
        LocalTime horarioChegada,
        boolean buscarNaResidencia,
        int acompanhantes,
        UUID idVeiculo,
        UUID autorId) {

    public AgendamentoCommandCriar(
            LocalDate data,
            UUID idPessoa,
            UUID idLista,
            UUID idRegraCadastro,
            UUID idTipoVeiculo,
            UUID idDestino,
            LocalTime horarioChegada,
            boolean buscarNaResidencia,
            int acompanhantes, UUID autorId) {
        this(data, idPessoa, idLista, idRegraCadastro, idTipoVeiculo, idDestino,
                horarioChegada, buscarNaResidencia, acompanhantes, null, autorId);
    }
}
