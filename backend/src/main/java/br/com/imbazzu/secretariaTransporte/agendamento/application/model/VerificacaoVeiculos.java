package br.com.imbazzu.secretariaTransporte.agendamento.application.model;

import java.util.List;

public record VerificacaoVeiculos(
        List<VeiculoPermitido> veiculosPermitidos,
        List<String> motivosReprovacao) {
}
