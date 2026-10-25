package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto;

import java.util.List;

public record VerificacaoVeiculosResponseDto(
        List<VeiculoPermitidoResponseDto> veiculosPermitidos,
        List<String> motivosReprovacao) {
}
