package br.com.imbazzu.frontSpring.agendamento.core.dto;

import java.util.List;
import java.util.UUID;

public record VeiculoPermitidoDto(UUID idTipoVeiculo, String nome, int capacidade, List<UUID> idsRegrasAprovadas) {
}
