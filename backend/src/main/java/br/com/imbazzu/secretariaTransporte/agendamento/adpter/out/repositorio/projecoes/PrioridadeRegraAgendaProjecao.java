package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.lista.core.domain.EnumPrioridade;

public interface PrioridadeRegraAgendaProjecao {

    UUID getId();

    EnumPrioridade getPrioridade();
}
