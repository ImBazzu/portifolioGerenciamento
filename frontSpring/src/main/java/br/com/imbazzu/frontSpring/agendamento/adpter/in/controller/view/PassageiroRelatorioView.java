package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view;

import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;

public record PassageiroRelatorioView(
        AgendamentoResponseDto agendamento,
        PassageiroDetalheView pessoa,
        String lista,
        VeiculoAbertoResponseDto veiculo) {
}
