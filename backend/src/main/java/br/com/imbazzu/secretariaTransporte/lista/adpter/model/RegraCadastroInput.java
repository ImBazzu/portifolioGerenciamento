package br.com.imbazzu.secretariaTransporte.lista.adpter.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


public record RegraCadastroInput(
        List<UUID> idsVeiculos,
        List<UUID> idsCidades,
        List<UUID> prioridades,
        LocalTime inicioHorario,
        LocalTime finalHorario,
        boolean obrigacaoAnexo,
        String descricao,
        List<DayOfWeek> diasSemana,
        Integer intervaloRepetacao) {
}
