package br.com.imbazzu.secretariaTransporte.veiculo.application.command.atualizar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.DestinoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.PessoaAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.RegraCadastroAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.TipoVeiculoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;

class AgendamentoCommandAtualizarHandlerTest {
    private final AgendamentoCommandPorta comandos = mock(AgendamentoCommandPorta.class);
    private final AgendamentoQueryPorta consultas = mock(AgendamentoQueryPorta.class);
    private final AgendamentoCommandAtualizarHandler handler = new AgendamentoCommandAtualizarHandler(comandos, consultas);
    private final UUID tipoId = UUID.randomUUID();
    private final UUID destinoId = UUID.randomUUID();
    private final UUID prioridadeId = UUID.randomUUID();
    private final Agendamento atual = Agendamento.criar(
            LocalDate.of(2026, 12, 1), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            tipoId, destinoId, Duration.ZERO, Duration.ZERO, LocalTime.of(10, 0), true, 2);

    @Test
    void preservaDadosOriginaisAoEditar() {
        prepararConsultas(3);

        handler.handler(comando(true, 2));

        var captor = ArgumentCaptor.forClass(Agendamento.class);
        verify(comandos).salvar(captor.capture());
        assertTrue(captor.getValue().isBuscarNaResidencia());
        assertEquals(2, captor.getValue().getAcompanhantes());
        assertEquals(LocalTime.of(11, 0), captor.getValue().getHorarioChegada());
    }

    @Test
    void rejeitaAlteracaoDaBuscaNaResidencia() {
        when(consultas.buscarAgendamento(atual.getId())).thenReturn(Optional.of(atual));
        when(consultas.diaAberto(atual.getData())).thenReturn(true);

        assertThrows(DadosInvalidosException.class, () -> handler.handler(comando(false, 2)));
        verify(comandos, never()).salvar(any(Agendamento.class));
    }

    @Test
    void permiteReduzirAcompanhantesAteZero() {
        prepararConsultas(3);

        handler.handler(comando(true, 0));

        var captor = ArgumentCaptor.forClass(Agendamento.class);
        verify(comandos).salvar(captor.capture());
        assertEquals(0, captor.getValue().getAcompanhantes());
        assertEquals(atual.getId(), captor.getValue().getId());
        assertTrue(captor.getValue().isBuscarNaResidencia());
    }

    @Test
    void permiteAumentarAcompanhantesAteOMaximoDaPrioridade() {
        prepararConsultas(3);
        var comUmAcompanhante = Agendamento.recriar(atual.getId(), atual.getData(), atual.getIdPessoa(),
                atual.getIdLista(), atual.getIdRegraCadastro(), tipoId, destinoId, Duration.ZERO,
                Duration.ZERO, atual.getHorarioChegada(), true, 1, null);
        when(consultas.buscarAgendamento(atual.getId())).thenReturn(Optional.of(comUmAcompanhante));

        handler.handler(comando(true, 2));
        var captor = ArgumentCaptor.forClass(Agendamento.class);
        verify(comandos).salvar(captor.capture());
        assertEquals(2, captor.getValue().getAcompanhantes());
    }

    @Test
    void rejeitaQuantidadeNegativaOuAcimaDaPrioridade() {
        prepararConsultas(5);

        assertThrows(DadosInvalidosException.class, () -> handler.handler(comando(true, -1)));
        assertThrows(DadosInvalidosException.class, () -> handler.handler(comando(true, 3)));
        verify(comandos, never()).salvar(any(Agendamento.class));
    }

    @Test
    void consideraQuantidadeAtualizadaNaCapacidadeDoTipo() {
        prepararConsultas(2);

        assertThrows(DadosInvalidosException.class, () -> handler.handler(comando(true, 2)));
        handler.handler(comando(true, 1));
        var captor = ArgumentCaptor.forClass(Agendamento.class);
        verify(comandos).salvar(captor.capture());
        assertEquals(1, captor.getValue().getAcompanhantes());
    }

    @Test
    void rejeitaAumentoQueExcedeACapacidadeDoVeiculoAtribuido() {
        prepararConsultas(5);
        when(consultas.buscarPessoa(atual.getIdPessoa()))
                .thenReturn(new PessoaAgendaDados(prioridadeId, true, true, true, 3));
        var veiculo = new VeiculoAberto(UUID.randomUUID(), atual.getData(), tipoId, "Carro", 4,
                "Carro 1", null, true);
        atual.atribuirVeiculo(veiculo.id());
        var outro = Agendamento.criar(atual.getData(), UUID.randomUUID(), atual.getIdLista(),
                atual.getIdRegraCadastro(), tipoId, destinoId, Duration.ZERO, Duration.ZERO,
                atual.getHorarioChegada(), false, 0);
        outro.atribuirVeiculo(veiculo.id());
        when(consultas.listarVeiculos(atual.getData())).thenReturn(List.of(veiculo));
        when(consultas.listarAgendamentos(atual.getData())).thenReturn(List.of(atual, outro));

        assertThrows(OperacaoNaoPermitidaException.class, () -> handler.handler(
                new AgendamentoCommandAtualizar(atual.getId(), tipoId, destinoId, LocalTime.of(10, 0),
                        true, 3, veiculo.id())));
        verify(comandos, never()).salvar(any(Agendamento.class));
    }

    @Test
    void atualizaAcompanhantesDePassageiroAtribuidoSemContarDuasVezes() {
        prepararConsultas(4);
        var veiculo = new VeiculoAberto(UUID.randomUUID(), atual.getData(), tipoId, "Carro", 4,
                "Carro 1", null, true);
        atual.atribuirVeiculo(veiculo.id());
        var outro = Agendamento.criar(atual.getData(), UUID.randomUUID(), atual.getIdLista(),
                atual.getIdRegraCadastro(), tipoId, destinoId, Duration.ZERO, Duration.ZERO,
                atual.getHorarioChegada(), false, 0);
        outro.atribuirVeiculo(veiculo.id());
        when(consultas.listarVeiculos(atual.getData())).thenReturn(List.of(veiculo));
        when(consultas.listarAgendamentos(atual.getData())).thenReturn(List.of(atual, outro));

        handler.handler(new AgendamentoCommandAtualizar(atual.getId(), tipoId, destinoId,
                LocalTime.of(10, 0), true, 1, veiculo.id()));

        var captor = ArgumentCaptor.forClass(Agendamento.class);
        verify(comandos).salvar(captor.capture());
        assertEquals(1, captor.getValue().getAcompanhantes());
        assertEquals(veiculo.id(), captor.getValue().getIdVeiculoAberto());
    }

    @Test
    void mantemQuantidadeAnteriorMesmoSePrioridadeFoiReduzida() {
        prepararConsultas(3);
        when(consultas.buscarPessoa(atual.getIdPessoa()))
                .thenReturn(new PessoaAgendaDados(prioridadeId, true, true, true, 1));

        handler.handler(comando(true, 2));
        var captor = ArgumentCaptor.forClass(Agendamento.class);
        verify(comandos).salvar(captor.capture());
        assertEquals(2, captor.getValue().getAcompanhantes());
    }

    private AgendamentoCommandAtualizar comando(boolean busca, int acompanhantes) {
        return new AgendamentoCommandAtualizar(
                atual.getId(), tipoId, destinoId, LocalTime.of(11, 0), busca, acompanhantes, null);
    }

    private void prepararConsultas(int capacidade) {
        when(consultas.buscarAgendamento(atual.getId())).thenReturn(Optional.of(atual));
        when(consultas.diaAberto(atual.getData())).thenReturn(true);
        when(consultas.buscarPessoa(atual.getIdPessoa())).thenReturn(new PessoaAgendaDados(prioridadeId, true, true, true, 2));
        when(consultas.buscarDestino(destinoId)).thenReturn(new DestinoAgendaDados(UUID.randomUUID(), Duration.ZERO, true));
        when(consultas.listaAtiva(atual.getIdLista())).thenReturn(true);
        when(consultas.buscarTipoVeiculo(tipoId)).thenReturn(new TipoVeiculoAgendaDados(tipoId, "Carro", capacidade, true));
        when(consultas.listarRegrasCadastro(atual.getIdLista())).thenReturn(List.of(
                new RegraCadastroAgendaDados(UUID.randomUUID(), List.of(prioridadeId), List.of(tipoId),
                        List.of(), false, null, null, List.of())));
        when(consultas.buscarToleranciaSeparacao(atual.getIdLista(), tipoId)).thenReturn(Optional.of(Duration.ZERO));
        when(consultas.listarVeiculos(atual.getData())).thenReturn(List.of());
    }
}
