package br.com.imbazzu.frontSpring.agendamento.core.dto;

import java.time.LocalTime;
import java.util.UUID;
import br.com.imbazzu.frontSpring.veiculo.core.FinalidadeAberturaVeiculo;

public record AbrirVeiculoRequestDto(
        UUID idTipoVeiculo,
        String identificacao,
        LocalTime horarioSaida,
        FinalidadeAberturaVeiculo finalidadeAbertura,
        UUID idListaAbertura) {

    public AbrirVeiculoRequestDto(UUID idTipoVeiculo, String identificacao, LocalTime horarioSaida) {
        this(idTipoVeiculo, identificacao, horarioSaida, FinalidadeAberturaVeiculo.AJUSTE, null);
    }
}
