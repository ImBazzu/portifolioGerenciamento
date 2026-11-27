package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto;

import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.FinalidadeAberturaVeiculo;

public record AbrirVeiculoRequestDto(
        @NotNull UUID idTipoVeiculo,
        @Size(max = 80) String identificacao,
        LocalTime horarioSaida,
        @NotNull FinalidadeAberturaVeiculo finalidadeAbertura,
        UUID idListaAbertura) {

    public AbrirVeiculoRequestDto(UUID idTipoVeiculo, String identificacao, LocalTime horarioSaida) {
        this(idTipoVeiculo, identificacao, horarioSaida, FinalidadeAberturaVeiculo.AJUSTE, null);
    }
}
