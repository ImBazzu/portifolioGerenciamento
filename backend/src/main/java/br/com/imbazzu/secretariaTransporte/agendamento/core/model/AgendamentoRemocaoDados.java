package br.com.imbazzu.secretariaTransporte.agendamento.core.model;

import java.util.UUID;

public record AgendamentoRemocaoDados(UUID idVeiculoAberto, boolean cancelado) {
}
