package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.FinalidadeAberturaVeiculo;

public record VeiculoAbertoResponseDto(
        UUID id,
        LocalDate data,
        UUID idTipoVeiculo,
        String nomeTipoVeiculo,
        int capacidade,
        String identificacao,
        LocalTime horarioSaida,
        LocalDateTime saidaConfirmadaEm,
        FinalidadeAberturaVeiculo finalidadeAbertura,
        UUID idListaAbertura,
        int assentosOcupados,
        List<AgendamentoResponseDto> passageiros) {

    public VeiculoAbertoResponseDto(
            UUID id, LocalDate data, UUID idTipoVeiculo, String nomeTipoVeiculo,
            int capacidade, String identificacao, LocalTime horarioSaida,
            LocalDateTime saidaConfirmadaEm, int assentosOcupados, List<AgendamentoResponseDto> passageiros) {
        this(id, data, idTipoVeiculo, nomeTipoVeiculo, capacidade, identificacao, horarioSaida,
                saidaConfirmadaEm, FinalidadeAberturaVeiculo.AJUSTE, null, assentosOcupados, passageiros);
    }
}
