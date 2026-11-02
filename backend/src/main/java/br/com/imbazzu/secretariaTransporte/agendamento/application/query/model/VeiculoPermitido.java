package br.com.imbazzu.secretariaTransporte.agendamento.application.query.model;

import java.util.List;
import java.util.UUID;

public record VeiculoPermitido(
        UUID idTipoVeiculo,
        String nome,
        int capacidade,
        List<UUID> idsRegrasAprovadas) {
}
