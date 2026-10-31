package br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.lista.core.domain.EnumPrioridade;

public interface RegraCadastroProjecao {

    UUID getId();

    List<UUID> getIdsVeiculos();

    List<UUID> getIdsCidades();

    List<EnumPrioridade> getPrioridades();

    LocalTime getInicioHorario();

    LocalTime getFinalHorario();

    boolean isObrigacaoAnexo();

    String getDescricao();
}
