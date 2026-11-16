package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view;

import java.util.UUID;

public record DestinoView(UUID id, UUID idCidade, String cidade, String nome) {
}
