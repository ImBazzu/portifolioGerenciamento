package br.com.imbazzu.secretariaTransporte.agendamento.application.query.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.FinalidadeAberturaVeiculo;

public record VeiculoAgenda(
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
        List<AgendamentoAgenda> passageiros) {
}
