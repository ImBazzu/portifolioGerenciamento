package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes;

import java.util.UUID;

public interface TipoVeiculoAgendaProjecao {

    UUID getId();

    String getNome();

    int getCapacidade();

    boolean isAtivo();
}
