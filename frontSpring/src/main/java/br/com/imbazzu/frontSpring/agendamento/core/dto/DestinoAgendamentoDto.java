package br.com.imbazzu.frontSpring.agendamento.core.dto;

import java.util.UUID;

public record DestinoAgendamentoDto(UUID id, UUID idCidade, String nome) {
}
