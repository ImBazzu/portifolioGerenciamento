package br.com.imbazzu.frontSpring.agendamento.core.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import br.com.imbazzu.frontSpring.veiculo.core.FinalidadeAberturaVeiculo;

public record VeiculoAbertoResponseDto(
        UUID id,
        LocalDate data,
        UUID idTipoVeiculo,
        String nomeTipoVeiculo,
        int capacidade,
        String identificacao,
        LocalTime horarioSaida,
        int assentosOcupados,
        List<AgendamentoResponseDto> passageiros,
        LocalDateTime saidaConfirmadaEm,
        FinalidadeAberturaVeiculo finalidadeAbertura,
        UUID idListaAbertura) {

    public VeiculoAbertoResponseDto(
            UUID id, LocalDate data, UUID idTipoVeiculo, String nomeTipoVeiculo,
            int capacidade, String identificacao, LocalTime horarioSaida,
            int assentosOcupados, List<AgendamentoResponseDto> passageiros) {
        this(id, data, idTipoVeiculo, nomeTipoVeiculo, capacidade, identificacao,
                horarioSaida, assentosOcupados, passageiros, null, FinalidadeAberturaVeiculo.AJUSTE, null);
    }

    public VeiculoAbertoResponseDto(
            UUID id, LocalDate data, UUID idTipoVeiculo, String nomeTipoVeiculo,
            int capacidade, String identificacao, LocalTime horarioSaida,
            int assentosOcupados, List<AgendamentoResponseDto> passageiros,
            LocalDateTime saidaConfirmadaEm) {
        this(id, data, idTipoVeiculo, nomeTipoVeiculo, capacidade, identificacao, horarioSaida,
                assentosOcupados, passageiros, saidaConfirmadaEm, FinalidadeAberturaVeiculo.AJUSTE, null);
    }

    public String faixaHorarioSaida() {
        if (horarioSaida == null) {
            return "INDEFINIDA";
        }
        int minutos = horarioSaida.getHour() * 60 + horarioSaida.getMinute();
        return minutos < 330 || minutos >= 1200 ? "ANTES_0530" : "DEPOIS_0530";
    }
}
