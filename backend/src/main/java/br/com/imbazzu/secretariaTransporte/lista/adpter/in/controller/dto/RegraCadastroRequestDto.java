package br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.lista.core.domain.EnumPrioridade;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RegraCadastroRequestDto(
        @NotEmpty List<@NotNull UUID> idsVeiculos,
        List<@NotNull UUID> idsCidades,
        @NotEmpty List<@NotNull EnumPrioridade> prioridades,
        LocalTime inicioHorario,
        LocalTime finalHorario,
        boolean obrigacaoAnexo,
        @jakarta.validation.constraints.Size(max = 249) String descricao) {
}
