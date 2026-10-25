package br.com.imbazzu.frontSpring.agendamento.core.dto;

import java.util.List;
import java.util.UUID;

public record ResultadoSeparacaoResponseDto(int passageirosAlocados, List<PassageiroPendenteDto> passageirosPendentes) {

    public record PassageiroPendenteDto(UUID idAgendamento, String motivo) {
    }
}
