package br.com.imbazzu.secretariaTransporte.agendamento.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.imbazzu.secretariaTransporte.agendamento.application.command.criar.AgendamentoCommandCriar;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.criar.AgendamentoCommandCriarHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.separarAutomaticamente.AgendamentoCommandSepararAutomaticamente;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.separarAutomaticamente.AgendamentoCommandSepararAutomaticamenteHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.listarVeiculosCompativeis.AgendamentoQueryListarVeiculosCompativeis;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.listarVeiculosCompativeis.AgendamentoQueryListarVeiculosCompativeisHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.buscarDia.AgendamentoQueryBuscarDia;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.buscarDia.AgendamentoQueryBuscarDiaHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.model.VerificacaoVeiculos;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.verificarVeiculos.AgendamentoQueryVerificarVeiculos;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.verificarVeiculos.AgendamentoQueryVerificarVeiculosHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.VeiculoAberto;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.DestinoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.PessoaAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.RegraCadastroAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.TipoVeiculoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;

@ExtendWith(MockitoExtension.class)
class AgendamentoHandlersTest {

    @Mock
    private AgendamentoCommandPorta comandos;

    @Mock
    private AgendamentoQueryPorta consultas;

    private AgendamentoQueryVerificarVeiculosHandler verificarVeiculosHandler;
    private AgendamentoCommandCriarHandler criarHandler;
    private AgendamentoQueryListarVeiculosCompativeisHandler listarVeiculosCompativeisHandler;
    private AgendamentoCommandSepararAutomaticamenteHandler separarAutomaticamenteHandler;

    @BeforeEach
    void configurarHandlers() {
        verificarVeiculosHandler = new AgendamentoQueryVerificarVeiculosHandler(consultas);
        criarHandler = new AgendamentoCommandCriarHandler(comandos, consultas);
        listarVeiculosCompativeisHandler = new AgendamentoQueryListarVeiculosCompativeisHandler(consultas);
        separarAutomaticamenteHandler = new AgendamentoCommandSepararAutomaticamenteHandler(comandos, consultas);
    }

    @Test
    void verificarSemVeiculosAbertosRetornaOrientacaoEmVezDeErro() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarVeiculos(data)).thenReturn(List.of());
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(UUID.randomUUID(), Duration.ofMinutes(30), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);

        var resultado = verificar(data, idPessoa, idLista, idDestino, LocalTime.of(9, 0), 0);

        assertTrue(resultado.veiculosPermitidos().isEmpty());
        assertTrue(resultado.motivosReprovacao().getFirst().contains("Abra pelo menos um veículo"));
    }

    @Test
    void verificaTiposPelasRegrasCompativeisEIgnoraRegraQueExigeAnexo() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var tipoPermitido = UUID.randomUUID();
        var tipoComAnexo = UUID.randomUUID();
        var regraIncompativel = UUID.randomUUID();
        var regraValida = UUID.randomUUID();
        var regraComAnexo = UUID.randomUUID();

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarVeiculos(data)).thenReturn(List.of(
                new VeiculoAberto(UUID.randomUUID(), data, tipoPermitido, "Carro", 4, null, null, true)));
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofMinutes(30), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(regraIncompativel, List.of("ALTA"), List.of(tipoPermitido),
                        List.of(idCidade), false, null, null),
                new RegraCadastroAgendaDados(regraValida, List.of("MODERADA"), List.of(tipoPermitido),
                        List.of(idCidade), false, LocalTime.of(8, 0), LocalTime.of(12, 0)),
                new RegraCadastroAgendaDados(regraComAnexo, List.of("MODERADA"), List.of(tipoComAnexo),
                        List.of(idCidade), true, null, null)));
        when(consultas.buscarTipoVeiculo(tipoPermitido))
                .thenReturn(new TipoVeiculoAgendaDados(tipoPermitido, "Carro", 4, true));
        when(consultas.buscarToleranciaSeparacao(idLista, tipoPermitido))
                .thenReturn(Optional.of(Duration.ofMinutes(15)));

        var resultado = verificar(data, idPessoa, idLista, idDestino, LocalTime.of(9, 0), 1);

        assertEquals(1, resultado.veiculosPermitidos().size());
        assertEquals(tipoPermitido, resultado.veiculosPermitidos().getFirst().idTipoVeiculo());
        assertEquals(List.of(regraValida), resultado.veiculosPermitidos().getFirst().idsRegrasAprovadas());
        assertTrue(resultado.motivosReprovacao().stream().anyMatch(motivo -> motivo.contains("exige anexo")));
    }

    @Test
    void verificaRegrasComSomenteLimiteInicialOuFinal() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var tipoId = UUID.randomUUID();
        var regraId = UUID.randomUUID();
        var veiculo = new VeiculoAberto(UUID.randomUUID(), data, tipoId, "Carro", 4, null, null, true);

        prepararVerificacao(data, idPessoa, idLista, idDestino, idCidade, tipoId,
                new RegraCadastroAgendaDados(regraId, List.of("MODERADA"), List.of(tipoId),
                        List.of(idCidade), false, null, LocalTime.of(12, 0)), veiculo);
        var dentroDoLimiteFinal = verificar(data, idPessoa, idLista, idDestino, LocalTime.of(9, 0), 0);
        var foraDoLimiteFinal = verificar(data, idPessoa, idLista, idDestino, LocalTime.of(13, 0), 0);
        assertEquals(List.of(tipoId), dentroDoLimiteFinal.veiculosPermitidos().stream()
                .map(item -> item.idTipoVeiculo()).toList());
        assertTrue(foraDoLimiteFinal.veiculosPermitidos().isEmpty());

        prepararVerificacao(data, idPessoa, idLista, idDestino, idCidade, tipoId,
                new RegraCadastroAgendaDados(regraId, List.of("MODERADA"), List.of(tipoId),
                        List.of(idCidade), false, LocalTime.of(8, 0), null), veiculo);
        var dentroDoLimiteInicial = verificar(data, idPessoa, idLista, idDestino, LocalTime.of(9, 0), 0);
        assertEquals(List.of(tipoId), dentroDoLimiteInicial.veiculosPermitidos().stream()
                .map(item -> item.idTipoVeiculo()).toList());
    }

    @Test
    void permiteCriarQuandoAoMenosUmaRegraAprovaOTipo() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var regraIncompativel = UUID.randomUUID();
        var regraCompativel = UUID.randomUUID();
        var idAgendamento = UUID.randomUUID();
        var veiculo = new VeiculoAberto(UUID.randomUUID(), data, idTipo, "Carro", 4, null, null, true);

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarVeiculos(data)).thenReturn(List.of(veiculo));
        when(consultas.existeAgendamento(data, idPessoa)).thenReturn(false);
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofMinutes(30), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);
        when(consultas.buscarTipoVeiculo(idTipo))
                .thenReturn(new TipoVeiculoAgendaDados(idTipo, "Carro", 4, true));
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(regraIncompativel, List.of("ALTA"), List.of(idTipo),
                        List.of(idCidade), false, null, null),
                new RegraCadastroAgendaDados(regraCompativel, List.of("MODERADA"), List.of(idTipo),
                        List.of(idCidade), false, LocalTime.of(8, 0), LocalTime.of(12, 0))));
        when(consultas.buscarToleranciaSeparacao(idLista, idTipo))
                .thenReturn(Optional.of(Duration.ofMinutes(15)));
        when(comandos.salvar(any(Agendamento.class))).thenReturn(idAgendamento);

        assertEquals(idAgendamento, criar(data, idPessoa, idLista, regraCompativel, idTipo,
                idDestino, LocalTime.of(9, 0), false, 0));
    }

    @Test
    void rejeitaAgendamentoQuandoTipoNaoFoiAbertoNaAgenda() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var tipoEscolhido = UUID.randomUUID();

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarVeiculos(data)).thenReturn(List.of(
                new VeiculoAberto(UUID.randomUUID(), data, UUID.randomUUID(), "Van", 8, null, null, false)));
        when(consultas.existeAgendamento(data, idPessoa)).thenReturn(false);
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(UUID.randomUUID(), Duration.ofMinutes(30), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);
        when(consultas.buscarTipoVeiculo(tipoEscolhido))
                .thenReturn(new TipoVeiculoAgendaDados(tipoEscolhido, "Carro", 4, true));

        assertThrows(OperacaoNaoPermitidaException.class, () -> criar(data, idPessoa, idLista,
                UUID.randomUUID(), tipoEscolhido, idDestino, LocalTime.of(9, 0), false, 0));
    }

    @Test
    void listaSomenteVeiculosFisicosCompativeisComCapacidadeEHorario() {
        var data = LocalDate.of(2026, 10, 25);
        var tipoId = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var cheio = new VeiculoAberto(UUID.randomUUID(), data, tipoId, "Carro", 1, "Cheio", null, true);
        var horarioIncompativel = new VeiculoAberto(
                UUID.randomUUID(), data, tipoId, "Carro", 4, "Fora do horário", LocalTime.of(8, 0), false);
        var disponivel = new VeiculoAberto(UUID.randomUUID(), data, tipoId, "Carro", 4, "Disponível", null, true);
        var pendente = agendamento(data, idLista, tipoId, idDestino, LocalTime.of(10, 0));
        var ocupante = agendamento(data, idLista, tipoId, idDestino, LocalTime.of(10, 40));
        ocupante.atribuirVeiculo(cheio.id());
        var passageiroHorario = agendamento(data, idLista, tipoId, idDestino, LocalTime.of(10, 0));
        passageiroHorario.atribuirVeiculo(horarioIncompativel.id());

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarAgendamentos(data)).thenReturn(List.of(pendente, ocupante, passageiroHorario));
        when(consultas.listarVeiculos(data)).thenReturn(List.of(cheio, horarioIncompativel, disponivel));

        Map<UUID, List<UUID>> compativeis = listarVeiculosCompativeisHandler.handler(
                new AgendamentoQueryListarVeiculosCompativeis(data));

        assertEquals(List.of(disponivel.id()), compativeis.get(pendente.getId()));
    }

    @Test
    void separacaoAutomaticaAgrupaPassageirosDentroDaTolerancia() {
        var data = LocalDate.of(2026, 10, 25);
        var tipoId = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var carro = new VeiculoAberto(UUID.randomUUID(), data, tipoId, "Carro", 4, "Carro 1", null, true);
        var primeiro = agendamento(data, idLista, tipoId, idDestino, LocalTime.of(9, 30));
        var segundo = agendamento(data, idLista, tipoId, idDestino, LocalTime.of(9, 40));
        var agendamentos = new ArrayList<>(List.of(primeiro, segundo));

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarAgendamentos(data)).thenReturn(agendamentos);
        when(consultas.listarVeiculos(data)).thenReturn(List.of(carro));
        when(comandos.salvar(any(Agendamento.class))).thenAnswer(invocation ->
                invocation.getArgument(0, Agendamento.class).getId());

        var resultado = separarAutomaticamenteHandler.handler(
                new AgendamentoCommandSepararAutomaticamente(data));

        assertEquals(2, resultado.passageirosAlocados());
        assertTrue(resultado.passageirosPendentes().isEmpty());
        assertEquals(carro.id(), primeiro.getIdVeiculoAberto());
        assertEquals(carro.id(), segundo.getIdVeiculoAberto());
        assertEquals(LocalTime.of(8, 30),
                CompatibilidadeVeiculoAgendamento.horarioSaidaDoVeiculo(carro, agendamentos));
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

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarAgendamentos(data)).thenReturn(new ArrayList<>(List.of(primeiro, segundo)));
        when(consultas.listarVeiculos(data)).thenReturn(List.of(carro));
        when(comandos.salvar(any(Agendamento.class))).thenAnswer(invocation ->
                invocation.getArgument(0, Agendamento.class).getId());

        var resultado = separarAutomaticamenteHandler.handler(
                new AgendamentoCommandSepararAutomaticamente(data));

        assertEquals(1, resultado.passageirosAlocados());
        assertEquals(List.of(segundo.getId()), resultado.passageirosPendentes().stream()
                .map(pendente -> pendente.idAgendamento()).toList());
    }

    @Test
    void buscaDiaComPassageirosVeiculosECompatibilidade() {
        var data = LocalDate.of(2026, 10, 25);
        var tipoId = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var carro = new VeiculoAberto(UUID.randomUUID(), data, tipoId, "Carro", 4, "Carro 1", null, true);
        var passageiroAlocado = agendamento(data, idLista, tipoId, idDestino, LocalTime.of(9, 30));
        passageiroAlocado.atribuirVeiculo(carro.id());
        var passageiroPendente = agendamento(data, idLista, tipoId, idDestino, LocalTime.of(9, 40));

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarAgendamentos(data)).thenReturn(List.of(passageiroAlocado, passageiroPendente));
        when(consultas.listarVeiculos(data)).thenReturn(List.of(carro));

        var agenda = new AgendamentoQueryBuscarDiaHandler(consultas)
                .handler(new AgendamentoQueryBuscarDia(data));

        assertEquals(1, agenda.veiculos().size());
        assertEquals(1, agenda.veiculos().getFirst().assentosOcupados());
        assertEquals(List.of(passageiroAlocado.getId()), agenda.veiculos().getFirst().passageiros().stream()
                .map(passageiro -> passageiro.id()).toList());
        assertEquals(LocalTime.of(8, 30), agenda.veiculos().getFirst().horarioSaida());
        assertEquals(1, agenda.passageirosPendentes().size());
        assertEquals(List.of(carro.id()), agenda.passageirosPendentes().getFirst().idsVeiculosCompativeis());
    }

    private VerificacaoVeiculos verificar(
            LocalDate data, UUID idPessoa, UUID idLista, UUID idDestino, LocalTime chegada, int acompanhantes) {
        return verificarVeiculosHandler.handler(new AgendamentoQueryVerificarVeiculos(
                data, idPessoa, idLista, idDestino, chegada, acompanhantes));
    }

    private UUID criar(
            LocalDate data,
            UUID idPessoa,
            UUID idLista,
            UUID idRegra,
            UUID idTipo,
            UUID idDestino,
            LocalTime chegada,
            boolean buscarNaResidencia,
            int acompanhantes) {
        return criarHandler.handler(new AgendamentoCommandCriar(data, idPessoa, idLista, idRegra,
                idTipo, idDestino, chegada, buscarNaResidencia, acompanhantes));
    }

    private static Agendamento agendamento(
            LocalDate data, UUID idLista, UUID idTipo, UUID idDestino, LocalTime chegada) {
        return Agendamento.criar(data, UUID.randomUUID(), idLista, UUID.randomUUID(),
                idTipo, idDestino, Duration.ofHours(1), Duration.ofMinutes(15), chegada, false, 0);
    }

    private void prepararVerificacao(
            LocalDate data,
            UUID idPessoa,
            UUID idLista,
            UUID idDestino,
            UUID idCidade,
            UUID tipoId,
            RegraCadastroAgendaDados regra,
            VeiculoAberto veiculo) {
        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarVeiculos(data)).thenReturn(List.of(veiculo));
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofMinutes(30), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(regra));
        when(consultas.buscarTipoVeiculo(tipoId))
                .thenReturn(new TipoVeiculoAgendaDados(tipoId, "Carro", 4, true));
        when(consultas.buscarToleranciaSeparacao(idLista, tipoId))
                .thenReturn(Optional.of(Duration.ofMinutes(15)));
    }
}
