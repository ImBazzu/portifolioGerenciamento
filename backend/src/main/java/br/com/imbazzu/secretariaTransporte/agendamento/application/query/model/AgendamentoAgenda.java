package br.com.imbazzu.secretariaTransporte.agendamento.application.query.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record AgendamentoAgenda(
        UUID id,
        LocalDate data,
        UUID idPessoa,
        UUID idLista,
        UUID idRegraCadastro,
        UUID idTipoVeiculo,
        UUID idDestino,
        LocalTime horarioChegada,
        LocalTime horarioSaidaNecessario,
        boolean buscarNaResidencia,
        int acompanhantes,
        UUID idVeiculoAberto,
        List<UUID> idsVeiculosCompativeis) {
}
