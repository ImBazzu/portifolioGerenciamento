package br.com.imbazzu.secretariaTransporte.agendamento.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.agendamento.application.command.separarAutomaticamente.AgendamentoCommandSepararAutomaticamente;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.separarAutomaticamente.AgendamentoCommandSepararAutomaticamenteHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.TipoVeiculoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;

class SeparacaoAutomaticaCidadesTest {
    private final LocalDate data = LocalDate.of(2030, 1, 2);
    private final UUID tipo = UUID.randomUUID();
    private final UUID cidadeA = UUID.randomUUID();
    private final UUID cidadeB = UUID.randomUUID();
    private final UUID cidadeC = UUID.randomUUID();
    private final UUID destinoA = UUID.randomUUID();
    private final UUID destinoB = UUID.randomUUID();
    private final UUID destinoC = UUID.randomUUID();
    private final Map<UUID, List<UUID>> destinos = Map.of(
            cidadeA, List.of(destinoA), cidadeB, List.of(destinoB), cidadeC, List.of(destinoC));

    @Test
    void exigeCompatibilidadeComTodosOsOcupantesSemInferirCombinacoesTransitivas() {
        var a = agendamento(destinoA, 10);
        var b = agendamento(destinoB, 10);
        var c = agendamento(destinoC, 10);
        var historico = Map.of(cidadeA, Set.of(cidadeB), cidadeB, Set.of(cidadeA, cidadeC),
                cidadeC, Set.of(cidadeB));

        assertTrue(CompatibilidadeVeiculoAgendamento.compativelCidade(destinos, Map.of(), List.of(a), a));
        assertFalse(CompatibilidadeVeiculoAgendamento.compativelCidade(destinos, Map.of(), List.of(a), b));
        assertTrue(CompatibilidadeVeiculoAgendamento.compativelCidade(destinos, historico, List.of(a), b));
        assertTrue(CompatibilidadeVeiculoAgendamento.compativelCidade(destinos, historico, List.of(b), a));
        assertFalse(CompatibilidadeVeiculoAgendamento.compativelCidade(destinos, historico, List.of(a, b), c));
    }

    @Test
    void historicoPermiteAlocacaoMasNaoDispensaCapacidadeOuHorario() {
        verificarAlocacao(0, 10, true, true);
        verificarAlocacao(0, 10, false, false);
        verificarAlocacao(3, 10, true, false);
        verificarAlocacao(0, 11, true, false);
    }

    private void verificarAlocacao(
            int acompanhantes, int horaNovo, boolean temHistorico, boolean usaVeiculoExistente) {
        var consultas = mock(AgendamentoQueryPorta.class);
        var comandos = mock(AgendamentoCommandPorta.class);
        var veiculo = new VeiculoAberto(UUID.randomUUID(), data, tipo, "Carro", 4, null, null, true);
        var ocupante = Agendamento.criar(data, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                tipo, destinoA, Duration.ZERO, Duration.ZERO, LocalTime.of(10, 0), false, acompanhantes);
        ocupante.atribuirVeiculo(veiculo.id());
        var novo = agendamento(destinoB, horaNovo);
        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarAgendamentos(data)).thenReturn(List.of(ocupante, novo));
        when(consultas.listarVeiculos(data)).thenReturn(List.of(veiculo));
        when(consultas.buscarTipoVeiculo(tipo)).thenReturn(new TipoVeiculoAgendaDados(tipo, "Carro", 4, true));
        when(consultas.buscarDestinosPorCidadeId(data)).thenReturn(destinos);
        when(consultas.buscarCidadesCompativeisAnteriores(data))
                .thenReturn(temHistorico ? Map.of(cidadeA, Set.of(cidadeB), cidadeB, Set.of(cidadeA)) : Map.of());
        when(comandos.salvar(any(VeiculoAberto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var resultado = new AgendamentoCommandSepararAutomaticamenteHandler(comandos, consultas)
                .handler(new AgendamentoCommandSepararAutomaticamente(data));

        assertEquals(1, resultado.passageirosAlocados());
        assertEquals(usaVeiculoExistente, veiculo.id().equals(novo.getIdVeiculoAberto()));
        verify(consultas).buscarCidadesCompativeisAnteriores(data);
    }

    private Agendamento agendamento(UUID destino, int hora) {
        return Agendamento.criar(data, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                tipo, destino, Duration.ZERO, Duration.ZERO, LocalTime.of(hora, 0), false, 0);
    }
}
