package br.com.imbazzu.frontSpring.agendamento.core.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record AgendamentoResponseDto(
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
