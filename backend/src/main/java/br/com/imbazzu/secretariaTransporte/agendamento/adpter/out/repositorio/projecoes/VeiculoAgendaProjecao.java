package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface VeiculoAgendaProjecao {

    UUID getId();

    LocalDate getData();

    UUID getIdTipoVeiculo();

    String getNomeTipoVeiculo();

    int getCapacidade();

    String getIdentificacao();

    LocalTime getHorarioSaida();

    boolean isHorarioSaidaCalculado();
}
