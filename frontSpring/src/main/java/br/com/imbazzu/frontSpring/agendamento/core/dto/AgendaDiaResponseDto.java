package br.com.imbazzu.frontSpring.agendamento.core.dto;

import java.time.LocalDate;
import java.util.List;

public record AgendaDiaResponseDto(
        LocalDate data,
        List<VeiculoAbertoResponseDto> veiculos,
        List<AgendamentoResponseDto> passageirosPendentes) {
}
