package br.com.imbazzu.secretariaTransporte.agendamento.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import br.com.imbazzu.secretariaTransporte.agendamento.application.command.criar.AgendamentoCommandCriar;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.criar.AgendamentoCommandCriarHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.verificarVeiculos.AgendamentoQueryVerificarVeiculos;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.verificarVeiculos.AgendamentoQueryVerificarVeiculosHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.DestinoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.PessoaAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.RegraCadastroAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.TipoVeiculoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;

class PrioridadeAgendamentoTest {
    private final AgendamentoQueryPorta consultas = mock(AgendamentoQueryPorta.class);
    private final AgendamentoCommandPorta comandos = mock(AgendamentoCommandPorta.class);
    private final LocalDate data = LocalDate.of(2026, 12, 1);
    private final LocalTime horario = LocalTime.of(10, 0);
    private final UUID pessoa = UUID.randomUUID();
    private final UUID lista = UUID.randomUUID();
    private final UUID tipo = UUID.randomUUID();
    private final UUID destino = UUID.randomUUID();
    private final UUID prioridade = UUID.randomUUID();

    @Test
    void criaAgendamentoComDadosDaPrioridadeERejeitaDivergencias() {
        preparar(3, prioridade);
        assertThrows(DadosInvalidosException.class, () -> new AgendamentoCommandCriarHandler(comandos, consultas)
                .handler(new AgendamentoCommandCriar(data, pessoa, lista, null, tipo, destino, horario, false, 0)));
        new AgendamentoCommandCriarHandler(comandos, consultas).handler(
                new AgendamentoCommandCriar(data, pessoa, lista, null, tipo, destino, horario, true, 2));
        var captor = ArgumentCaptor.forClass(Agendamento.class);
        verify(comandos).salvar(captor.capture());
        assertTrue(captor.getValue().isBuscarNaResidencia());
        assertEquals(2, captor.getValue().getAcompanhantes());
    }

    @Test
    void capacidadeEVerificacaoUsamQuantidadeDaPrioridade() {
        preparar(2, prioridade);
        var verificacao = new AgendamentoQueryVerificarVeiculosHandler(consultas).handler(
                new AgendamentoQueryVerificarVeiculos(data, pessoa, lista, destino, horario, 2));
        assertTrue(verificacao.veiculosPermitidos().isEmpty());
        assertThrows(DadosInvalidosException.class, () -> new AgendamentoCommandCriarHandler(comandos, consultas)
                .handler(new AgendamentoCommandCriar(data, pessoa, lista, null, tipo, destino, horario, true, 2)));
        verify(comandos, never()).salvar(any(Agendamento.class));
    }

    @Test
    void regraDeOutraPrioridadeNaoAprovaVeiculo() {
        preparar(4, UUID.randomUUID());
        var verificacao = new AgendamentoQueryVerificarVeiculosHandler(consultas).handler(
                new AgendamentoQueryVerificarVeiculos(data, pessoa, lista, destino, horario, 2));
        assertTrue(verificacao.veiculosPermitidos().isEmpty());
    }

    @Test
    void prioridadeArquivadaImpedeVerificacaoECriacao() {
        preparar(4, prioridade);
        when(consultas.buscarPessoa(pessoa)).thenReturn(new PessoaAgendaDados(prioridade, true, false, true, 2));
        assertThrows(OperacaoNaoPermitidaException.class, () -> new AgendamentoQueryVerificarVeiculosHandler(consultas)
                .handler(new AgendamentoQueryVerificarVeiculos(data, pessoa, lista, destino, horario, 2)));
        assertThrows(OperacaoNaoPermitidaException.class, () -> new AgendamentoCommandCriarHandler(comandos, consultas)
                .handler(new AgendamentoCommandCriar(data, pessoa, lista, null, tipo, destino, horario, true, 2)));
        verify(comandos, never()).salvar(any(Agendamento.class));
    }

    @Test
    void verificacaoRespeitaDiasDaSemanaDaRegra() {
        var quinta = LocalDate.of(2026, 11, 19);
        var sabado = LocalDate.of(2026, 11, 21);
        preparar(4, prioridade);
        when(consultas.diaAberto(quinta)).thenReturn(true);
        when(consultas.diaAberto(sabado)).thenReturn(true);
        when(consultas.listarVeiculos(quinta)).thenReturn(List.of());
        when(consultas.listarVeiculos(sabado)).thenReturn(List.of());
        when(consultas.listarRegrasCadastro(lista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(UUID.randomUUID(), List.of(prioridade), List.of(tipo),
                        List.of(), false, null, null, List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))));

        var verificacaoQuinta = new AgendamentoQueryVerificarVeiculosHandler(consultas).handler(
                new AgendamentoQueryVerificarVeiculos(quinta, pessoa, lista, destino, horario, 2));
        var verificacaoSabado = new AgendamentoQueryVerificarVeiculosHandler(consultas).handler(
                new AgendamentoQueryVerificarVeiculos(sabado, pessoa, lista, destino, horario, 2));

        assertTrue(verificacaoQuinta.veiculosPermitidos().isEmpty());
        assertTrue(verificacaoQuinta.motivosReprovacao().stream()
                .anyMatch(motivo -> motivo.contains("dia da semana")));
        assertEquals(1, verificacaoSabado.veiculosPermitidos().size());
    }

    private void preparar(int capacidade, UUID prioridadeRegra) {
        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarVeiculos(data)).thenReturn(List.of());
        when(consultas.buscarPessoa(pessoa)).thenReturn(new PessoaAgendaDados(prioridade, true, true, true, 2));
        when(consultas.buscarDestino(destino)).thenReturn(new DestinoAgendaDados(UUID.randomUUID(), Duration.ZERO, true));
        when(consultas.listaAtiva(lista)).thenReturn(true);
        when(consultas.buscarTipoVeiculo(tipo)).thenReturn(new TipoVeiculoAgendaDados(tipo, "Carro", capacidade, true));
        when(consultas.buscarToleranciaSeparacao(lista, tipo)).thenReturn(Optional.of(Duration.ZERO));
        when(consultas.listarRegrasCadastro(lista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(UUID.randomUUID(), List.of(prioridadeRegra), List.of(tipo),
                        List.of(), false, null, null, List.of())));
    }
}
