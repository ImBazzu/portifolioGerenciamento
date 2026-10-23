package br.com.imbazzu.secretariaTransporte.lista.application.model;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.lista.core.domain.EnumPrioridade;

public record RegraCadastroInput(
        List<UUID> idsVeiculos,
        List<UUID> idsCidades,
        List<EnumPrioridade> prioridades,
        LocalTime inicioHorario,
        LocalTime finalHorario,
        boolean obrigacaoAnexo,
        String descricao) {
}
