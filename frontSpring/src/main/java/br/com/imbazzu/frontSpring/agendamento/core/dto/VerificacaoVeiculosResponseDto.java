package br.com.imbazzu.frontSpring.agendamento.core.dto;

import java.util.List;

public record VerificacaoVeiculosResponseDto(
        List<VeiculoPermitidoDto> veiculosPermitidos,
        List<String> motivosReprovacao) {
}
