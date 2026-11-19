package br.com.imbazzu.frontSpring.agendamento.core.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record HistoricoViagemResponseDto(
        UUID id,
        LocalDate data,
        LocalTime horarioChegada,
        LocalTime horarioSaidaNecessario,
        String destinoNome,
        String listaDescricao,
        String tipoVeiculoNome,
        Boolean buscarNaResidencia,
        Integer acompanhantes) {
}
