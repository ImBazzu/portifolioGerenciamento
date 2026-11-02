package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes;

import java.time.LocalTime;
import java.util.UUID;

public interface RegraCadastroAgendaProjecao {

    UUID getId();

    boolean isObrigacaoAnexo();

    LocalTime getInicioHorario();

    LocalTime getFinalHorario();
}
