package br.com.imbazzu.secretariaTransporte.agendamento.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
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
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
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
    void verificaRegraComSomenteLimiteFinalSemFalhar() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var tipoId = UUID.randomUUID();
        var regraId = UUID.randomUUID();
        var veiculo = new VeiculoAberto(UUID.randomUUID(), data, tipoId, "Carro", 4, null, null, true);

        prepararVerificacao(data, idPessoa, idLista, idDestino, idCidade, tipoId, regraId,
                new RegraCadastroAgendaDados(regraId, List.of("MODERADA"), List.of(tipoId),
                        List.of(idCidade), false, null, LocalTime.of(12, 0)), veiculo);

        var resultado = application.verificarVeiculos(data, idPessoa, idLista, idDestino,
                LocalTime.of(9, 0), 0);
        var foraDoLimite = application.verificarVeiculos(data, idPessoa, idLista, idDestino,
                LocalTime.of(13, 0), 0);

        assertEquals(List.of(tipoId), resultado.veiculosPermitidos().stream()
                .map(veiculoPermitido -> veiculoPermitido.idTipoVeiculo())
                .toList());
        assertTrue(foraDoLimite.veiculosPermitidos().isEmpty());
    }

    @Test
    void verificaRegraComSomenteLimiteInicialSemFalhar() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var tipoId = UUID.randomUUID();
        var regraId = UUID.randomUUID();
        var veiculo = new VeiculoAberto(UUID.randomUUID(), data, tipoId, "Carro", 4, null, null, true);

        prepararVerificacao(data, idPessoa, idLista, idDestino, idCidade, tipoId, regraId,
                new RegraCadastroAgendaDados(regraId, List.of("MODERADA"), List.of(tipoId),
                        List.of(idCidade), false, LocalTime.of(8, 0), null), veiculo);

        var resultado = application.verificarVeiculos(data, idPessoa, idLista, idDestino,
                LocalTime.of(9, 0), 0);

        assertEquals(List.of(tipoId), resultado.veiculosPermitidos().stream()
                .map(veiculoPermitido -> veiculoPermitido.idTipoVeiculo())
                .toList());
    }

    @Test
    void naoOfereceVeiculoFisicoCheioOuForaDaToleranciaParaAtribuicaoManual() {
        var data = LocalDate.of(2026, 10, 25);
        var tipoId = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var veiculoIncompativel = new VeiculoAberto(
                UUID.randomUUID(), data, tipoId, "Carro", 4, "Carro 1", null, true);
        var veiculoCompativel = new VeiculoAberto(
                UUID.randomUUID(), data, tipoId, "Carro", 4, "Carro 2", null, true);
        var pendente = agendamento(data, idLista, tipoId, idDestino, LocalTime.of(10, 0));
        var ocupante = agendamento(data, idLista, tipoId, idDestino, LocalTime.of(10, 40));
        ocupante.atribuirVeiculo(veiculoIncompativel.id());

        when(porta.diaAberto(data)).thenReturn(true);
        when(porta.listarPorData(data)).thenReturn(List.of(pendente, ocupante));
        when(porta.listarVeiculos(data)).thenReturn(List.of(veiculoIncompativel, veiculoCompativel));

        Map<UUID, List<UUID>> compativeis = application.listarVeiculosCompativeis(data);

        assertEquals(List.of(veiculoCompativel.id()), compativeis.get(pendente.getId()));
    }

    @Test
    void rejeitaAgendamentoDiretoQuandoTipoNaoFoiAbertoNaAgenda() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idTipoEscolhido = UUID.randomUUID();
        var outroTipo = UUID.randomUUID();

        when(porta.diaAberto(data)).thenReturn(true);
        when(porta.listarVeiculos(data)).thenReturn(List.of(
                new VeiculoAberto(UUID.randomUUID(), data, outroTipo, "Vans", 8, null, null, true)));
        when(porta.existeAgendamento(data, idPessoa)).thenReturn(false);
        when(porta.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(porta.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(UUID.randomUUID(), Duration.ofMinutes(30), true));
        when(porta.listaAtiva(idLista)).thenReturn(true);
        when(porta.buscarTipoVeiculo(idTipoEscolhido))
                .thenReturn(new TipoVeiculoAgendaDados(idTipoEscolhido, "Carro baixo", 4, true));

        assertThrows(OperacaoNaoPermitidaException.class, () -> application.criar(
                data, idPessoa, idLista, UUID.randomUUID(), idTipoEscolhido, idDestino,
                LocalTime.of(9, 0), false, 0));
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

    private void prepararVerificacao(LocalDate data, UUID idPessoa, UUID idLista, UUID idDestino,
            UUID idCidade, UUID tipoId, UUID regraId, RegraCadastroAgendaDados regra,
            VeiculoAberto veiculo) {
        when(porta.diaAberto(data)).thenReturn(true);
        when(porta.listarVeiculos(data)).thenReturn(List.of(veiculo));
        when(porta.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(porta.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofMinutes(30), true));
        when(porta.listaAtiva(idLista)).thenReturn(true);
        when(porta.listarRegrasCadastro(idLista)).thenReturn(List.of(regra));
        when(porta.buscarTipoVeiculo(tipoId))
                .thenReturn(new TipoVeiculoAgendaDados(tipoId, "Carro", 4, true));
        when(porta.buscarToleranciaSeparacao(idLista, tipoId))
                .thenReturn(Optional.of(Duration.ofMinutes(15)));
    }
}
