package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto;

import java.time.LocalDate;
import java.util.List;

public record AgendaDiaResponseDto(
        LocalDate data,
        List<VeiculoAbertoResponseDto> veiculos,
        List<AgendamentoResponseDto> passageirosPendentes,
        List<AgendamentoCanceladoResponseDto> passageirosCancelados) {

    public AgendaDiaResponseDto(
            LocalDate data,
            List<VeiculoAbertoResponseDto> veiculos,
            List<AgendamentoResponseDto> passageirosPendentes) {
        this(data, veiculos, passageirosPendentes, List.of());
    }
}
