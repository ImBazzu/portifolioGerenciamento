package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto;

import java.util.List;
import java.util.UUID;

public record ResultadoSeparacaoResponseDto(
        int passageirosAlocados,
        List<PassageiroPendenteResponseDto> passageirosPendentes) {

    public record PassageiroPendenteResponseDto(UUID idAgendamento, String motivo) {
    }
}
