package br.com.imbazzu.secretariaTransporte.agendamento.core.model;

import java.util.UUID;

public record TipoVeiculoAgendaDados(UUID id, String nome, int capacidade, boolean ativo) {
}
