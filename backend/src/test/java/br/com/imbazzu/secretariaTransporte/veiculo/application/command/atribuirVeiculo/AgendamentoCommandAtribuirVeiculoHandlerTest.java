package br.com.imbazzu.secretariaTransporte.veiculo.application.command.atribuirVeiculo;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.FinalidadeAberturaVeiculo;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;

class AgendamentoCommandAtribuirVeiculoHandlerTest {
    @Test
    void veiculoEspecialNaoAceitaPassageiroDeOutraLista() {
        var consultas = mock(AgendamentoQueryPorta.class);
        var comandos = mock(AgendamentoCommandPorta.class);
        var data = LocalDate.of(2026, 12, 1);
        var tipo = UUID.randomUUID();
        var passageiro = Agendamento.criar(data, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                tipo, UUID.randomUUID(), Duration.ofHours(1), Duration.ZERO, LocalTime.of(10, 0), false, 0);
        var veiculo = new VeiculoAberto(UUID.randomUUID(), data, tipo, "Carro", 4, null,
                LocalTime.of(8, 0), false, null, FinalidadeAberturaVeiculo.ALTA, UUID.randomUUID());
        when(consultas.buscarAgendamento(passageiro.getId())).thenReturn(Optional.of(passageiro));
        when(consultas.buscarVeiculo(veiculo.id())).thenReturn(Optional.of(veiculo));

        assertThrows(OperacaoNaoPermitidaException.class, () ->
                new AgendamentoCommandAtribuirVeiculoHandler(comandos, consultas)
                        .handler(new AgendamentoCommandAtribuirVeiculo(passageiro.getId(), veiculo.id())));
        verify(comandos, never()).salvar(any(Agendamento.class));
    }

    @Test
    void veiculoEspecialNaoAceitaSaidaDepoisDaNecessaria() {
        var consultas = mock(AgendamentoQueryPorta.class);
        var comandos = mock(AgendamentoCommandPorta.class);
        var data = LocalDate.of(2026, 12, 1);
        var tipo = UUID.randomUUID();
        var lista = UUID.randomUUID();
        var passageiro = Agendamento.criar(data, UUID.randomUUID(), lista, UUID.randomUUID(),
                tipo, UUID.randomUUID(), Duration.ofHours(1), Duration.ZERO, LocalTime.of(10, 0), false, 0);
        var veiculo = new VeiculoAberto(UUID.randomUUID(), data, tipo, "Carro", 4, null,
                LocalTime.of(9, 30), false, null, FinalidadeAberturaVeiculo.EMERGENCIA, lista);
        when(consultas.buscarAgendamento(passageiro.getId())).thenReturn(Optional.of(passageiro));
        when(consultas.buscarVeiculo(veiculo.id())).thenReturn(Optional.of(veiculo));
        when(consultas.listarAgendamentos(data)).thenReturn(List.of(passageiro));
        var handler = new AgendamentoCommandAtribuirVeiculoHandler(comandos, consultas);

        assertThrows(OperacaoNaoPermitidaException.class,
                () -> handler.handler(new AgendamentoCommandAtribuirVeiculo(passageiro.getId(), veiculo.id())));
        verify(comandos, never()).salvar(any(Agendamento.class));

        var saidaCompativel = new VeiculoAberto(veiculo.id(), data, tipo, "Carro", 4, null,
                LocalTime.of(9, 0), false, null, FinalidadeAberturaVeiculo.ALTA, lista);
        when(consultas.buscarVeiculo(veiculo.id())).thenReturn(Optional.of(saidaCompativel));
        handler.handler(new AgendamentoCommandAtribuirVeiculo(passageiro.getId(), veiculo.id()));
        verify(comandos).salvar(passageiro);
    }
}
