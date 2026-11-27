package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto;

import jakarta.validation.constraints.Size;

public record AgendamentoCancelarRequestDto(
        @Size(max = 500, message = "O motivo do cancelamento deve ter no máximo 500 caracteres")
        String motivo) {
}
