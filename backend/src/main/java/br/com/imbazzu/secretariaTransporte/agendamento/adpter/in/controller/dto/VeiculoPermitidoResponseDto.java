package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto;

import java.util.List;
import java.util.UUID;

public record VeiculoPermitidoResponseDto(
        UUID idTipoVeiculo,
        String nome,
        int capacidade,
        List<UUID> idsRegrasAprovadas) {
}
