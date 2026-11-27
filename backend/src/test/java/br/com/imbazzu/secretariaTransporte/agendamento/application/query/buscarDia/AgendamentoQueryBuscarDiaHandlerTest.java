package br.com.imbazzu.secretariaTransporte.agendamento.application.query.buscarDia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.imbazzu.secretariaTransporte.agendamento.core.model.AgendamentoCanceladoDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;

@ExtendWith(MockitoExtension.class)
class AgendamentoQueryBuscarDiaHandlerTest {

    @Mock
    private AgendamentoQueryPorta consultas;

    @InjectMocks
    private AgendamentoQueryBuscarDiaHandler handler;

    @Test
    void incluiViagensCanceladasNaConsultaDoDia() {
        var data = LocalDate.of(2026, 11, 27);
        var cancelado = new AgendamentoCanceladoDados(
                UUID.randomUUID(), data, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), LocalTime.of(9, 0), false, 0, "Operador",
                LocalDateTime.of(2026, 11, 26, 18, 30), "Passageiro cancelou");
        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarAgendamentos(data)).thenReturn(List.of());
        when(consultas.listarVeiculos(data)).thenReturn(List.of());
        when(consultas.listarAgendamentosCancelados(data)).thenReturn(List.of(cancelado));

        var agenda = handler.handler(new AgendamentoQueryBuscarDia(data));

        assertEquals(List.of(cancelado), agenda.passageirosCancelados());
    }
}
