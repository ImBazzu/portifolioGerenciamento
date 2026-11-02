package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes;

import java.time.Duration;
import java.util.UUID;

public interface DestinoAgendaProjecao {

    UUID getIdCidade();

    Duration getTempoViagem();

    boolean isAtivo();
}
