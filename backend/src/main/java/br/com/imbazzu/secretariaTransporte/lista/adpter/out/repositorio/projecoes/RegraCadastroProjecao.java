package br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


public interface RegraCadastroProjecao {

    UUID getId();

    List<UUID> getIdsVeiculos();

    List<UUID> getIdsCidades();

    List<UUID> getPrioridades();

    LocalTime getInicioHorario();

    LocalTime getFinalHorario();

    boolean isObrigacaoAnexo();

    String getDescricao();

    List<DayOfWeek> getDiasSemana();

    Integer getIntervaloRepetacao();
}
