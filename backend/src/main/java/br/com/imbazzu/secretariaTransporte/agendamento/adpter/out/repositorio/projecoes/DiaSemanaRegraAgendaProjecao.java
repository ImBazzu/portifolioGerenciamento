package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes;

import java.time.DayOfWeek;
import java.util.UUID;

public interface DiaSemanaRegraAgendaProjecao {

    UUID getId();

    DayOfWeek getDiaSemana();
}
