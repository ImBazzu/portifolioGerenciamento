package br.com.imbazzu.secretariaTransporte.veiculo.application.command.confirmarSaida;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;

class AgendamentoCommandConfirmarSaidaHandlerTest {

    private final AgendamentoCommandPorta comandos = mock(AgendamentoCommandPorta.class);
    private final AgendamentoQueryPorta consultas = mock(AgendamentoQueryPorta.class);
    private final AgendamentoCommandConfirmarSaidaHandler handler =
            new AgendamentoCommandConfirmarSaidaHandler(comandos, consultas);

    @Test
    void confirmaVeiculoComHorarioDefinidoERegistraAutor() {
        UUID veiculoId = UUID.randomUUID();
        UUID autorId = UUID.randomUUID();
        var veiculo = veiculo(veiculoId, LocalTime.of(6, 0), false, null);
        when(consultas.buscarVeiculo(veiculoId)).thenReturn(Optional.of(veiculo));
        when(consultas.listarAgendamentos(veiculo.data())).thenReturn(List.of());
        when(comandos.confirmarSaidaVeiculo(any(), any(), any())).thenReturn(true);

        handler.handler(new AgendamentoCommandConfirmarSaida(veiculoId, autorId));

        verify(comandos).confirmarSaidaVeiculo(
                org.mockito.ArgumentMatchers.eq(veiculoId), any(), org.mockito.ArgumentMatchers.eq(autorId));
    }

    @Test
    void exigeHorarioCalculadoAntesDeConfirmarSaida() {
        UUID veiculoId = UUID.randomUUID();
        var veiculo = veiculo(veiculoId, null, true, null);
        when(consultas.buscarVeiculo(veiculoId)).thenReturn(Optional.of(veiculo));
        when(consultas.listarAgendamentos(veiculo.data())).thenReturn(List.of());

        assertThrows(OperacaoNaoPermitidaException.class,
                () -> handler.handler(new AgendamentoCommandConfirmarSaida(veiculoId, UUID.randomUUID())));

        verify(comandos, never()).confirmarSaidaVeiculo(any(), any(), any());
    }

    private VeiculoAberto veiculo(
            UUID id, LocalTime horarioSaida, boolean calculado, java.time.LocalDateTime confirmadaEm) {
        return new VeiculoAberto(
                id,
                LocalDate.of(2026, 12, 1),
                UUID.randomUUID(),
                "Carro",
                4,
                "Carro 01",
                horarioSaida,
                calculado,
                confirmadaEm);
    }
}
