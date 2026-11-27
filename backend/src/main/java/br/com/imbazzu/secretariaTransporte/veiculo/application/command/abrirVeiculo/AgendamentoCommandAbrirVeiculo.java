package br.com.imbazzu.secretariaTransporte.veiculo.application.command.abrirVeiculo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.FinalidadeAberturaVeiculo;

public record AgendamentoCommandAbrirVeiculo(
        LocalDate data,
        UUID idTipoVeiculo,
        String identificacao,
        LocalTime horarioSaida,
        FinalidadeAberturaVeiculo finalidadeAbertura,
        UUID idListaAbertura) {

    public AgendamentoCommandAbrirVeiculo(
            LocalDate data, UUID idTipoVeiculo, String identificacao, LocalTime horarioSaida) {
        this(data, idTipoVeiculo, identificacao, horarioSaida, FinalidadeAberturaVeiculo.AJUSTE, null);
    }
}
