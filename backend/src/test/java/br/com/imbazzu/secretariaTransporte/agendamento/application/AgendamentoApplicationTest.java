package br.com.imbazzu.secretariaTransporte.agendamento.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.VeiculoAberto;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.DestinoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.PessoaAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.RegraCadastroAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.TipoVeiculoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoPorta;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;

@ExtendWith(MockitoExtension.class)
class AgendamentoApplicationTest {

    @Mock
    private AgendamentoPorta porta;

    @InjectMocks
    private AgendamentoApplication application;

    @Test
    void verificaTiposPermitidosPelasRegrasEExcluiRegraQueExigeAnexo() {
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var tipoPermitido = UUID.randomUUID();
        var tipoComAnexo = UUID.randomUUID();
        var regraValida = UUID.randomUUID();
        var regraComAnexo = UUID.randomUUID();
        var data = LocalDate.of(2026, 10, 25);

        when(porta.diaAberto(data)).thenReturn(true);
        when(porta.listarVeiculos(data)).thenReturn(List.of(
                new VeiculoAberto(UUID.randomUUID(), data, tipoPermitido, "Carro", 4, null, null, true)));
        when(porta.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(porta.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofMinutes(30), true));
        when(porta.listaAtiva(idLista)).thenReturn(true);
        when(porta.listarRegrasCadastro(idLista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(regraValida, List.of("MODERADA"), List.of(tipoPermitido),
                        List.of(idCidade), false, LocalTime.of(8, 0), LocalTime.of(12, 0)),
                new RegraCadastroAgendaDados(regraComAnexo, List.of("MODERADA"), List.of(tipoComAnexo),
                        List.of(idCidade), true, null, null)));
        when(porta.buscarTipoVeiculo(tipoPermitido))
                .thenReturn(new TipoVeiculoAgendaDados(tipoPermitido, "Carro", 4, true));
        when(porta.buscarToleranciaSeparacao(idLista, tipoPermitido))
                .thenReturn(Optional.of(Duration.ofMinutes(15)));

        var resultado = application.verificarVeiculos(data, idPessoa, idLista,
                idDestino, LocalTime.of(9, 0), 1);

        assertEquals(1, resultado.veiculosPermitidos().size());
        assertEquals(tipoPermitido, resultado.veiculosPermitidos().getFirst().idTipoVeiculo());
        assertEquals(List.of(regraValida), resultado.veiculosPermitidos().getFirst().idsRegrasAprovadas());
        assertTrue(resultado.motivosReprovacao().stream().anyMatch(motivo -> motivo.contains("exige anexo")));
    }

    @Test
    void separacaoAutomaticaAgrupaPassageirosDentroDaToleranciaECalculaSaidaMaisCedo() {
        var data = LocalDate.of(2026, 10, 25);
        var tipoId = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var carro = new VeiculoAberto(UUID.randomUUID(), data, tipoId, "Carro", 4, "Carro 1", null, true);
        var primeiro = agendamento(data, idLista, tipoId, idDestino, LocalTime.of(9, 30));
        var segundo = agendamento(data, idLista, tipoId, idDestino, LocalTime.of(9, 40));
        var agendamentos = new java.util.ArrayList<>(List.of(primeiro, segundo));

        when(porta.diaAberto(data)).thenReturn(true);
        when(porta.listarPorData(data)).thenReturn(agendamentos);
        when(porta.listarVeiculos(data)).thenReturn(List.of(carro));
        when(porta.salvar(any(Agendamento.class))).thenAnswer(invocation ->
                invocation.getArgument(0, Agendamento.class).getId());

        var resultado = application.separarAutomaticamente(data);

        assertEquals(2, resultado.passageirosAlocados());
        assertTrue(resultado.passageirosPendentes().isEmpty());
        assertEquals(carro.id(), primeiro.getIdVeiculoAberto());
        assertEquals(carro.id(), segundo.getIdVeiculoAberto());
        assertEquals(LocalTime.of(8, 30), application.horarioSaidaDoVeiculo(carro));
    }

    @Test
    void separacaoAutomaticaNaoAgrupaPassageirosForaDaTolerancia() {
        var data = LocalDate.of(2026, 10, 25);
        var tipoId = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var carro = new VeiculoAberto(UUID.randomUUID(), data, tipoId, "Carro", 4, null, null, true);
        var primeiro = agendamento(data, idLista, tipoId, idDestino, LocalTime.of(9, 30));
        var segundo = agendamento(data, idLista, tipoId, idDestino, LocalTime.of(10, 0));

        when(porta.diaAberto(data)).thenReturn(true);
        when(porta.listarPorData(data)).thenReturn(new java.util.ArrayList<>(List.of(primeiro, segundo)));
        when(porta.listarVeiculos(data)).thenReturn(List.of(carro));
        when(porta.salvar(any(Agendamento.class))).thenAnswer(invocation ->
                invocation.getArgument(0, Agendamento.class).getId());

        var resultado = application.separarAutomaticamente(data);

        assertEquals(1, resultado.passageirosAlocados());
        assertEquals(List.of(segundo.getId()), resultado.passageirosPendentes().stream()
                .map(pendente -> pendente.idAgendamento())
                .toList());
    }

    private static Agendamento agendamento(LocalDate data, UUID idLista, UUID idTipo,
            UUID idDestino, LocalTime chegada) {
        return Agendamento.criar(data, UUID.randomUUID(), idLista, UUID.randomUUID(),
                idTipo, idDestino, Duration.ofHours(1), Duration.ofMinutes(15), chegada, false, 0);
    }
}
