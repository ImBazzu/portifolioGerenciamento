package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto;

import java.time.LocalDate;
import java.util.List;

public record AgendaDiaResponseDto(
        LocalDate data,
        List<VeiculoAbertoResponseDto> veiculos,
        List<AgendamentoResponseDto> passageirosPendentes) {
}
