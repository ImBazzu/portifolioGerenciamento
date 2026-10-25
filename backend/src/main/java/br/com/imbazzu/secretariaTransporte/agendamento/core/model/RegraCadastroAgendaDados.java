package br.com.imbazzu.secretariaTransporte.agendamento.core.model;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record RegraCadastroAgendaDados(
        UUID id,
        List<String> prioridades,
        List<UUID> idsVeiculos,
        List<UUID> idsCidades,
        boolean obrigacaoAnexo,
        LocalTime inicioHorario,
        LocalTime finalHorario) {
}
