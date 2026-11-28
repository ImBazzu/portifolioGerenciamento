package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.OpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.FinalidadeAberturaVeiculo;

class MontadorAgendaDiaTest {
    @Test
    void limitaPendentesDeCarrosEspeciaisAListaVinculada() {
        var data = LocalDate.of(2026, 12, 1);
        var lista = UUID.randomUUID();
        var outro = UUID.randomUUID();
        var especial = new VeiculoAbertoResponseDto(UUID.randomUUID(), data, UUID.randomUUID(),
                "Carro", 4, null, LocalTime.of(8, 0), 0, List.of(), null,
                FinalidadeAberturaVeiculo.ALTA, lista);
        var ajuste = new VeiculoAbertoResponseDto(UUID.randomUUID(), data, UUID.randomUUID(),
                "Carro", 4, null, null, 0, List.of());
        var permitido = passageiro(data, lista);
        var diferente = passageiro(data, outro);
        var agenda = new AgendaDiaResponseDto(data, List.of(especial, ajuste), List.of(permitido, diferente));
        var model = new ExtendedModelMap();

        new MontadorAgendaDia().dividirPorTipo(model, agenda,
                new OpcoesAgendamento(List.of(), List.of(), List.of(), List.of()));

        var pendentes = (Map<?, ?>) model.get("passageirosPendentesPorVeiculo");
        assertEquals(List.of(permitido), pendentes.get(especial.id()));
        assertEquals(List.of(permitido, diferente), pendentes.get(ajuste.id()));
    }

    private static AgendamentoResponseDto passageiro(LocalDate data, UUID lista) {
        return new AgendamentoResponseDto(UUID.randomUUID(), data, UUID.randomUUID(), lista,
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalTime.NOON,
                LocalTime.NOON, false, 0, null, List.of());
    }
}
