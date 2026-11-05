package br.com.imbazzu.secretariaTransporte.agendamento.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

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
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.atualizar.AgendamentoCommandAtualizar;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.atualizar.AgendamentoCommandAtualizarHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.atribuirVeiculo.AgendamentoCommandAtribuirVeiculo;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.atribuirVeiculo.AgendamentoCommandAtribuirVeiculoHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.atualizarVeiculo.AgendamentoCommandAtualizarVeiculo;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.atualizarVeiculo.AgendamentoCommandAtualizarVeiculoHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.removerVeiculoAberto.AgendamentoCommandRemoverVeiculoAberto;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.removerVeiculoAberto.AgendamentoCommandRemoverVeiculoAbertoHandler;
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
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.DestinoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.PessoaAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.RegraCadastroAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.TipoVeiculoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;

@ExtendWith(MockitoExtension.class)
class AgendamentoHandlersTest {

    @Mock
    private AgendamentoCommandPorta comandos;

    @Mock
    private AgendamentoQueryPorta consultas;

    private AgendamentoQueryVerificarVeiculosHandler verificarVeiculosHandler;
    private AgendamentoCommandCriarHandler criarHandler;
    private AgendamentoCommandAtualizarHandler atualizarHandler;
    private AgendamentoQueryListarVeiculosCompativeisHandler listarVeiculosCompativeisHandler;
    private AgendamentoCommandSepararAutomaticamenteHandler separarAutomaticamenteHandler;

    @BeforeEach
    void configurarHandlers() {
        verificarVeiculosHandler = new AgendamentoQueryVerificarVeiculosHandler(consultas);
        criarHandler = new AgendamentoCommandCriarHandler(comandos, consultas);
        atualizarHandler = new AgendamentoCommandAtualizarHandler(comandos, consultas);
        listarVeiculosCompativeisHandler = new AgendamentoQueryListarVeiculosCompativeisHandler(consultas);
        separarAutomaticamenteHandler = new AgendamentoCommandSepararAutomaticamenteHandler(comandos, consultas);
    }

    @Test
    void verificaCarroPermitidoMesmoSemVeiculosAbertos() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var idRegra = UUID.randomUUID();

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarVeiculos(data)).thenReturn(List.of());
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofMinutes(30), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(idRegra, List.of("MODERADA"), List.of(idTipo),
                        List.of(idCidade), false, null, null)));
        when(consultas.buscarTipoVeiculo(idTipo))
                .thenReturn(new TipoVeiculoAgendaDados(idTipo, "Carro", 4, true));
        when(consultas.buscarToleranciaSeparacao(idLista, idTipo))
                .thenReturn(Optional.of(Duration.ofMinutes(15)));

        var resultado = verificar(data, idPessoa, idLista, idDestino, LocalTime.of(9, 0), 0);

        assertEquals(List.of(idTipo), resultado.veiculosPermitidos().stream()
                .map(tipo -> tipo.idTipoVeiculo()).toList());
    }

    @Test
    void verificaVanNaoPermitidaSemVeiculoFisicoAberto() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var idRegra = UUID.randomUUID();

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarVeiculos(data)).thenReturn(List.of());
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofMinutes(30), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(idRegra, List.of("MODERADA"), List.of(idTipo),
                        List.of(idCidade), false, null, null)));
        when(consultas.buscarTipoVeiculo(idTipo))
                .thenReturn(new TipoVeiculoAgendaDados(idTipo, "Van", 15, true));

        var resultado = verificar(data, idPessoa, idLista, idDestino, LocalTime.of(9, 0), 0);

        assertTrue(resultado.veiculosPermitidos().isEmpty());
        assertTrue(resultado.motivosReprovacao().stream()
                .anyMatch(motivo -> motivo.contains("Nenhum veículo físico do tipo Van")));
    }

    @Test
    void verificaVanPermitidaSemRegraDeTolerancia() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var idRegra = UUID.randomUUID();
        var veiculo = new VeiculoAberto(
                UUID.randomUUID(), data, idTipo, "Van", 15, "Van 1", LocalTime.of(7, 0), false);

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarVeiculos(data)).thenReturn(List.of(veiculo));
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofMinutes(30), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(idRegra, List.of("MODERADA"), List.of(idTipo),
                        List.of(idCidade), false, null, null)));
        when(consultas.buscarTipoVeiculo(idTipo))
                .thenReturn(new TipoVeiculoAgendaDados(idTipo, "Van", 15, true));

        var resultado = verificar(data, idPessoa, idLista, idDestino, LocalTime.of(9, 0), 0);

        assertEquals(List.of(idTipo), resultado.veiculosPermitidos().stream()
                .map(tipo -> tipo.idTipoVeiculo()).toList());
    }

    @Test
    void explicaQuandoRegraCompativelNaoPossuiTiposDeVeiculoVinculados() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idRegra = UUID.randomUUID();
        var idTipo = UUID.randomUUID();

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarVeiculos(data)).thenReturn(List.of(
                new VeiculoAberto(UUID.randomUUID(), data, idTipo, "Van", 8, null, LocalTime.of(9, 0), false)));
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(UUID.randomUUID(), Duration.ofMinutes(30), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(idRegra, List.of("MODERADA"), List.of(), List.of(),
                        false, null, null)));

        var resultado = verificar(data, idPessoa, idLista, idDestino, LocalTime.of(9, 0), 0);

        assertTrue(resultado.veiculosPermitidos().isEmpty());
        assertTrue(resultado.motivosReprovacao().stream()
                .anyMatch(motivo -> motivo.contains(idRegra.toString())
                        && motivo.contains("não possui tipos de veículo vinculados")));
    }

    @Test
    void explicaQuaisCriteriosDaRegraNaoCorrespondemAoAgendamento() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidadeRegra = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var idRegra = UUID.randomUUID();

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarVeiculos(data)).thenReturn(List.of(
                new VeiculoAberto(UUID.randomUUID(), data, idTipo, "Van", 8, null, LocalTime.of(9, 0), false)));
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(UUID.randomUUID(), Duration.ofMinutes(30), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(idRegra, List.of("ALTA"), List.of(idTipo),
                        List.of(idCidadeRegra), false, LocalTime.of(8, 0), LocalTime.of(10, 0))));

        var resultado = verificar(data, idPessoa, idLista, idDestino, LocalTime.of(11, 0), 0);

        assertTrue(resultado.veiculosPermitidos().isEmpty());
        var motivo = resultado.motivosReprovacao().getFirst();
        assertTrue(motivo.contains(idRegra.toString()));
        assertTrue(motivo.contains("prioridade da pessoa (MODERADA) não está entre as aceitas: [ALTA]"));
        assertTrue(motivo.contains("cidade do destino ("));
        assertTrue(motivo.contains("horário 11:00 fora do intervalo permitido (de 08:00 até 10:00)"));
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
        when(comandos.salvar(any(Agendamento.class))).thenAnswer(invocation -> {
            var agendamento = invocation.getArgument(0, Agendamento.class);
            assertEquals(regraCompativel, agendamento.getIdRegraCadastro());
            return idAgendamento;
        });

        assertEquals(idAgendamento, criarHandler.handler(new AgendamentoCommandCriar(
                data, idPessoa, idLista, null, idTipo, idDestino, LocalTime.of(9, 0), false, 0)));
    }

    @Test
    void criaPassageiroDeVanJaAtribuidoAoVeiculoFisicoSelecionado() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var idRegra = UUID.randomUUID();
        var idAgendamento = UUID.randomUUID();
        var veiculo = new VeiculoAberto(
                UUID.randomUUID(), data, idTipo, "Van", 8, "Van 1", LocalTime.of(7, 0), false);

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarVeiculos(data)).thenReturn(List.of(veiculo));
        when(consultas.existeAgendamento(data, idPessoa)).thenReturn(false);
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofMinutes(30), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);
        when(consultas.buscarTipoVeiculo(idTipo)).thenReturn(new TipoVeiculoAgendaDados(idTipo, "Van", 8, true));
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(
                        idRegra, List.of("MODERADA"), List.of(idTipo), List.of(idCidade), false, null, null)));
        when(consultas.listarAgendamentos(data)).thenReturn(List.of());
        when(comandos.salvar(any(Agendamento.class))).thenAnswer(invocation -> {
            var agendamento = invocation.getArgument(0, Agendamento.class);
            assertEquals(veiculo.id(), agendamento.getIdVeiculoAberto());
            assertEquals(Duration.ZERO, agendamento.getToleranciaSeparacao());
            return idAgendamento;
        });

        var resultado = criarHandler.handler(new AgendamentoCommandCriar(
                data, idPessoa, idLista, idRegra, idTipo, idDestino,
                LocalTime.of(9, 0), false, 0, veiculo.id()));

        assertEquals(idAgendamento, resultado);
    }

    @Test
    void atualizaAgendamentoDeVanSemExigirTolerancia() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestinoOriginal = UUID.randomUUID();
        var idNovoDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var idRegra = UUID.randomUUID();
        var veiculo = new VeiculoAberto(
                UUID.randomUUID(), data, idTipo, "Van", 15, "Van 1", LocalTime.of(7, 0), false);
        var atual = Agendamento.criar(
                data, idPessoa, idLista, idRegra, idTipo, idDestinoOriginal,
                Duration.ofMinutes(30), Duration.ZERO, LocalTime.of(9, 0), false, 0);
        atual.atribuirVeiculo(veiculo.id(), idTipo, Duration.ZERO);

        when(consultas.buscarAgendamento(atual.getId())).thenReturn(Optional.of(atual));
        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idNovoDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofMinutes(45), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);
        when(consultas.buscarTipoVeiculo(idTipo)).thenReturn(new TipoVeiculoAgendaDados(idTipo, "Van", 15, true));
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(idRegra, List.of("MODERADA"), List.of(idTipo),
                        List.of(idCidade), false, null, null)));
        when(consultas.listarVeiculos(data)).thenReturn(List.of(veiculo));
        when(consultas.listarAgendamentos(data)).thenReturn(List.of(atual));

        atualizarHandler.handler(new AgendamentoCommandAtualizar(
                atual.getId(), idTipo, idNovoDestino, LocalTime.of(10, 0), true, 2, veiculo.id()));

        org.mockito.Mockito.verify(comandos).salvar(org.mockito.ArgumentMatchers.argThat(
                (Agendamento agendamento) ->
                agendamento.getId().equals(atual.getId())
                        && agendamento.getIdDestino().equals(idNovoDestino)
                        && agendamento.getHorarioChegada().equals(LocalTime.of(10, 0))
                        && agendamento.isBuscarNaResidencia()
                        && agendamento.getAcompanhantes() == 2
                        && agendamento.getIdVeiculoAberto().equals(veiculo.id())
                        && agendamento.getTempoViagem().equals(Duration.ofMinutes(45))
                        && agendamento.getToleranciaSeparacao().isZero()));
    }

    @Test
    void atualizarCarroContinuaExigindoToleranciaDaLista() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var idTipoCarro = UUID.randomUUID();
        var idTipoVan = UUID.randomUUID();
        var idRegra = UUID.randomUUID();
        var atual = Agendamento.criar(
                data, idPessoa, idLista, idRegra, idTipoVan, idDestino,
                Duration.ofMinutes(30), Duration.ZERO, LocalTime.of(9, 0), false, 0);

        when(consultas.buscarAgendamento(atual.getId())).thenReturn(Optional.of(atual));
        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofMinutes(30), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);
        when(consultas.buscarTipoVeiculo(idTipoCarro))
                .thenReturn(new TipoVeiculoAgendaDados(idTipoCarro, "Carro", 4, true));
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(idRegra, List.of("MODERADA"),
                        List.of(idTipoCarro), List.of(idCidade), false, null, null)));
        when(consultas.buscarToleranciaSeparacao(idLista, idTipoCarro)).thenReturn(Optional.empty());

        assertThrows(OperacaoNaoPermitidaException.class, () -> atualizarHandler.handler(
                new AgendamentoCommandAtualizar(
                        atual.getId(), idTipoCarro, idDestino, LocalTime.of(9, 0), false, 0, null)));
    }

    @Test
    void atualizaIdentificacaoEHorarioDeSaidaSemAlterarTipoDoVeiculo() {
        var data = LocalDate.of(2026, 10, 25);
        var idTipo = UUID.randomUUID();
        var idVeiculo = UUID.randomUUID();
        var veiculo = new VeiculoAberto(
                idVeiculo, data, idTipo, "Van", 15, "Van 1", LocalTime.of(8, 0), false);
        when(consultas.buscarVeiculo(idVeiculo)).thenReturn(Optional.of(veiculo));
        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarAgendamentos(data)).thenReturn(List.of());
        when(comandos.salvar(any(VeiculoAberto.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        var atualizado = new AgendamentoCommandAtualizarVeiculoHandler(comandos, consultas)
                .handler(new AgendamentoCommandAtualizarVeiculo(
                        idVeiculo, " Van 02 ", LocalTime.of(8, 30)));

        assertEquals(idVeiculo, atualizado.id());
        assertEquals(idTipo, atualizado.idTipoVeiculo());
        assertEquals("Van 02", atualizado.identificacao());
        assertEquals(LocalTime.of(8, 30), atualizado.horarioSaida());
        verify(comandos).salvar(atualizado);
    }

    @Test
    void rejeitaHorarioDeSaidaQueNaoAtendePassageirosAtribuidos() {
        var data = LocalDate.of(2026, 10, 25);
        var idVeiculo = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idRegra = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var veiculo = new VeiculoAberto(
                idVeiculo, data, idTipo, "Van", 15, "Van 1", LocalTime.of(8, 0), false);
        var passageiro = Agendamento.criar(
                data, idPessoa, idLista, idRegra, idTipo, idDestino,
                Duration.ofMinutes(30), Duration.ZERO, LocalTime.of(9, 0), false, 0);
        passageiro.atribuirVeiculo(idVeiculo);
        when(consultas.buscarVeiculo(idVeiculo)).thenReturn(Optional.of(veiculo));
        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarAgendamentos(data)).thenReturn(List.of(passageiro));

        assertThrows(OperacaoNaoPermitidaException.class, () ->
                new AgendamentoCommandAtualizarVeiculoHandler(comandos, consultas)
                        .handler(new AgendamentoCommandAtualizarVeiculo(
                                idVeiculo, "Van 1", LocalTime.of(8, 45))));
    }

    @Test
    void excluiVeiculoAbertoEDesatribuiPassageirosSemExcluirAgendamentos() {
        var data = LocalDate.of(2026, 10, 25);
        var idVeiculo = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var passageiroAtribuido = Agendamento.criar(
                data, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), idTipo, UUID.randomUUID(),
                Duration.ofMinutes(30), Duration.ZERO, LocalTime.of(9, 0), false, 0);
        passageiroAtribuido.atribuirVeiculo(idVeiculo);
        var pendente = Agendamento.criar(
                data, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), idTipo, UUID.randomUUID(),
                Duration.ofMinutes(30), Duration.ZERO, LocalTime.of(10, 0), false, 0);
        when(consultas.buscarVeiculo(idVeiculo)).thenReturn(Optional.of(new VeiculoAberto(
                idVeiculo, data, idTipo, "Van", 15, "Van 1", LocalTime.of(8, 0), false)));
        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarAgendamentos(data)).thenReturn(List.of(passageiroAtribuido, pendente));

        new AgendamentoCommandRemoverVeiculoAbertoHandler(comandos, consultas)
                .handler(new AgendamentoCommandRemoverVeiculoAberto(idVeiculo));

        assertNull(passageiroAtribuido.getIdVeiculoAberto());
        assertNull(pendente.getIdVeiculoAberto());
        verify(comandos).salvar(passageiroAtribuido);
        verify(comandos).deletarVeiculo(idVeiculo);
    }

    @Test
    void criaPassageiroDeCarroPendenteMesmoSemVeiculoAberto() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var idRegra = UUID.randomUUID();
        var idAgendamento = UUID.randomUUID();

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarVeiculos(data)).thenReturn(List.of());
        when(consultas.existeAgendamento(data, idPessoa)).thenReturn(false);
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofMinutes(30), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);
        when(consultas.buscarTipoVeiculo(idTipo))
                .thenReturn(new TipoVeiculoAgendaDados(idTipo, "Carro", 4, true));
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(idRegra, List.of("MODERADA"), List.of(idTipo),
                        List.of(idCidade), false, null, null)));
        when(consultas.buscarToleranciaSeparacao(idLista, idTipo))
                .thenReturn(Optional.of(Duration.ofMinutes(15)));
        when(comandos.salvar(any(Agendamento.class))).thenAnswer(invocation -> {
            var agendamento = invocation.getArgument(0, Agendamento.class);
            assertNull(agendamento.getIdVeiculoAberto());
            return idAgendamento;
        });

        var resultado = criar(data, idPessoa, idLista, idRegra, idTipo, idDestino,
                LocalTime.of(9, 0), false, 0);

        assertEquals(idAgendamento, resultado);
    }

    @Test
    void rejeitaAgendamentoDeVanSemVeiculoFisicoSelecionado() {
        var data = LocalDate.of(2026, 10, 25);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var idRegra = UUID.randomUUID();

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarVeiculos(data)).thenReturn(List.of(
                new VeiculoAberto(UUID.randomUUID(), data, idTipo, "Van", 8, null, null, false)));
        when(consultas.existeAgendamento(data, idPessoa)).thenReturn(false);
        when(consultas.buscarPessoa(idPessoa)).thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofMinutes(30), true));
        when(consultas.listaAtiva(idLista)).thenReturn(true);
        when(consultas.buscarTipoVeiculo(idTipo))
                .thenReturn(new TipoVeiculoAgendaDados(idTipo, "Van", 8, true));
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(
                new RegraCadastroAgendaDados(idRegra, List.of("MODERADA"), List.of(idTipo),
                        List.of(idCidade), false, null, null)));
        var excecao = assertThrows(OperacaoNaoPermitidaException.class, () -> criar(data, idPessoa, idLista,
                idRegra, idTipo, idDestino, LocalTime.of(9, 0), false, 0));
        assertTrue(excecao.getMessage().contains("Selecione o veículo físico"));
    }

    @Test
    void listaSomenteVeiculosFisicosCompativeisComCapacidadeEHorario() {
        var data = LocalDate.of(2026, 10, 25);
        var tipoId = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var cheio = new VeiculoAberto(UUID.randomUUID(), data, tipoId, "Van", 1, "Cheio", LocalTime.of(7, 0), false);
        var horarioIncompativel = new VeiculoAberto(
                UUID.randomUUID(), data, tipoId, "Van", 4, "Fora do horário", LocalTime.of(9, 1), false);
        var disponivel = new VeiculoAberto(
                UUID.randomUUID(), data, tipoId, "Van", 4, "Disponível", LocalTime.of(8, 0), false);
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
    void listaVeiculosDeTodosOsTiposPermitidosPelaRegra() {
        var data = LocalDate.of(2026, 10, 25);
        var idLista = UUID.randomUUID();
        var idRegra = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idPessoaPendente = UUID.randomUUID();
        var idPessoaOcupante = UUID.randomUUID();
        var tipoSelecionado = UUID.randomUUID();
        var tipoAlternativo = UUID.randomUUID();
        var veiculoCheio = new VeiculoAberto(
                UUID.randomUUID(), data, tipoSelecionado, "Carro", 1, "Cheio", null, true);
        var veiculoAlternativo = new VeiculoAberto(
                UUID.randomUUID(), data, tipoAlternativo, "Van", 8, "Van disponível", LocalTime.of(7, 0), false);
        var regra = new RegraCadastroAgendaDados(
                idRegra, List.of("MODERADA"), List.of(tipoSelecionado, tipoAlternativo),
                List.of(idCidade), false, null, null);
        var pendente = Agendamento.criar(
                data, idPessoaPendente, idLista, idRegra, tipoSelecionado, idDestino,
                Duration.ofHours(1), Duration.ofMinutes(15), LocalTime.of(10, 0), false, 0);
        var ocupante = Agendamento.criar(
                data, idPessoaOcupante, idLista, idRegra, tipoSelecionado, idDestino,
                Duration.ofHours(1), Duration.ofMinutes(15), LocalTime.of(10, 0), false, 0);
        ocupante.atribuirVeiculo(veiculoCheio.id());

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarAgendamentos(data)).thenReturn(List.of(pendente, ocupante));
        when(consultas.listarVeiculos(data)).thenReturn(List.of(veiculoCheio, veiculoAlternativo));
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(regra));
        when(consultas.buscarTipoVeiculo(tipoSelecionado))
                .thenReturn(new TipoVeiculoAgendaDados(tipoSelecionado, "Carro", 4, true));
        when(consultas.buscarTipoVeiculo(tipoAlternativo))
                .thenReturn(new TipoVeiculoAgendaDados(tipoAlternativo, "Van", 8, true));
        when(consultas.buscarPessoa(idPessoaPendente))
                .thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarPessoa(idPessoaOcupante))
                .thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofHours(1), true));
        when(consultas.buscarToleranciaSeparacao(idLista, tipoSelecionado))
                .thenReturn(Optional.of(Duration.ofMinutes(15)));

        var compativeis = listarVeiculosCompativeisHandler.handler(
                new AgendamentoQueryListarVeiculosCompativeis(data));

        assertEquals(List.of(veiculoAlternativo.id()), compativeis.get(pendente.getId()));
    }

    @Test
    void permiteAtribuirManualmentePassageiroDeCarroEmVan() {
        var data = LocalDate.of(2026, 10, 25);
        var idLista = UUID.randomUUID();
        var idRegra = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idPessoa = UUID.randomUUID();
        var tipoSelecionado = UUID.randomUUID();
        var tipoAlternativo = UUID.randomUUID();
        var veiculo = new VeiculoAberto(
                UUID.randomUUID(), data, tipoAlternativo, "Van", 8, "Van", LocalTime.of(9, 0), false);
        var agendamento = Agendamento.criar(
                data, idPessoa, idLista, idRegra, tipoSelecionado, idDestino,
                Duration.ofHours(1), Duration.ofMinutes(15), LocalTime.of(10, 0), false, 0);
        when(consultas.buscarAgendamento(agendamento.getId())).thenReturn(Optional.of(agendamento));
        when(consultas.buscarVeiculo(veiculo.id())).thenReturn(Optional.of(veiculo));
        when(consultas.listarAgendamentos(data)).thenReturn(List.of(agendamento));

        new AgendamentoCommandAtribuirVeiculoHandler(comandos, consultas)
                .handler(new AgendamentoCommandAtribuirVeiculo(agendamento.getId(), veiculo.id()));

        assertEquals(veiculo.id(), agendamento.getIdVeiculoAberto());
        assertEquals(tipoAlternativo, agendamento.getIdTipoVeiculo());
        assertEquals(Duration.ZERO, agendamento.getToleranciaSeparacao());
        verify(comandos).salvar(agendamento);
    }

    @Test
    void permiteAtribuicaoManualIgnorandoToleranciaDeHorarioDoCarro() {
        var data = LocalDate.of(2026, 10, 25);
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var carro = new VeiculoAberto(
                UUID.randomUUID(), data, idTipo, "Carro", 4, "Carro 1", null, true);
        var ocupante = Agendamento.criar(
                data, UUID.randomUUID(), idLista, UUID.randomUUID(), idTipo, idDestino,
                Duration.ofHours(1), Duration.ofMinutes(15), LocalTime.of(9, 0), false, 0);
        ocupante.atribuirVeiculo(carro.id());
        var pendente = Agendamento.criar(
                data, UUID.randomUUID(), idLista, UUID.randomUUID(), idTipo, idDestino,
                Duration.ofHours(1), Duration.ofMinutes(15), LocalTime.of(12, 0), false, 0);
        when(consultas.buscarAgendamento(pendente.getId())).thenReturn(Optional.of(pendente));
        when(consultas.buscarVeiculo(carro.id())).thenReturn(Optional.of(carro));
        when(consultas.listarAgendamentos(data)).thenReturn(List.of(ocupante, pendente));
        when(consultas.buscarToleranciaSeparacao(idLista, idTipo))
                .thenReturn(Optional.of(Duration.ofMinutes(15)));

        new AgendamentoCommandAtribuirVeiculoHandler(comandos, consultas)
                .handler(new AgendamentoCommandAtribuirVeiculo(pendente.getId(), carro.id()));

        assertEquals(carro.id(), pendente.getIdVeiculoAberto());
        verify(comandos).salvar(pendente);
    }

    @Test
    void rejeitaAtribuicaoManualQueUltrapassaCapacidadeDoVeiculo() {
        var data = LocalDate.of(2026, 10, 25);
        var idLista = UUID.randomUUID();
        var carro = new VeiculoAberto(
                UUID.randomUUID(), data, UUID.randomUUID(), "Carro", 1, "Carro lotado", null, true);
        var ocupante = Agendamento.criar(
                data, UUID.randomUUID(), idLista, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                Duration.ofHours(1), Duration.ZERO, LocalTime.of(10, 0), false, 0);
        ocupante.atribuirVeiculo(carro.id());
        var pendente = Agendamento.criar(
                data, UUID.randomUUID(), idLista, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                Duration.ofHours(1), Duration.ZERO, LocalTime.of(10, 0), false, 0);
        when(consultas.buscarAgendamento(pendente.getId())).thenReturn(Optional.of(pendente));
        when(consultas.buscarVeiculo(carro.id())).thenReturn(Optional.of(carro));
        when(consultas.listarAgendamentos(data)).thenReturn(List.of(ocupante, pendente));

        assertThrows(OperacaoNaoPermitidaException.class, () ->
                new AgendamentoCommandAtribuirVeiculoHandler(comandos, consultas)
                        .handler(new AgendamentoCommandAtribuirVeiculo(pendente.getId(), carro.id())));
    }

    @Test
    void separacaoAutomaticaAbreCarroPermitidoSemUsarVan() {
        var data = LocalDate.of(2026, 10, 25);
        var idLista = UUID.randomUUID();
        var idRegra = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idPessoa = UUID.randomUUID();
        var tipoSelecionado = UUID.randomUUID();
        var tipoAlternativo = UUID.randomUUID();
        var veiculo = new VeiculoAberto(
                UUID.randomUUID(), data, tipoAlternativo, "Van", 8, "Van", LocalTime.of(9, 0), false);
        var regra = new RegraCadastroAgendaDados(
                idRegra, List.of("MODERADA"), List.of(tipoSelecionado, tipoAlternativo),
                List.of(idCidade), false, null, null);
        var agendamento = Agendamento.criar(
                data, idPessoa, idLista, idRegra, tipoSelecionado, idDestino,
                Duration.ofHours(1), Duration.ofMinutes(15), LocalTime.of(10, 0), false, 0);

        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.listarAgendamentos(data)).thenReturn(new ArrayList<>(List.of(agendamento)));
        when(consultas.listarVeiculos(data)).thenReturn(List.of(veiculo));
        when(consultas.buscarTipoVeiculo(tipoSelecionado))
                .thenReturn(new TipoVeiculoAgendaDados(tipoSelecionado, "Carro", 4, true));
        when(consultas.listarRegrasCadastro(idLista)).thenReturn(List.of(regra));
        when(consultas.buscarPessoa(idPessoa))
                .thenReturn(new PessoaAgendaDados(EnumPrioridade.MODERADA, true));
        when(consultas.buscarDestino(idDestino))
                .thenReturn(new DestinoAgendaDados(idCidade, Duration.ofHours(1), true));
        when(consultas.buscarToleranciaSeparacao(idLista, tipoSelecionado))
                .thenReturn(Optional.of(Duration.ofMinutes(15)));
        when(consultas.buscarTipoVeiculo(tipoAlternativo))
                .thenReturn(new TipoVeiculoAgendaDados(tipoAlternativo, "Van", 8, true));
        when(comandos.salvar(any(VeiculoAberto.class))).thenAnswer(invocation ->
                invocation.getArgument(0, VeiculoAberto.class));
        when(comandos.salvar(any(Agendamento.class))).thenAnswer(invocation ->
                invocation.getArgument(0, Agendamento.class).getId());

        var resultado = separarAutomaticamenteHandler.handler(
                new AgendamentoCommandSepararAutomaticamente(data));

        assertEquals(1, resultado.passageirosAlocados());
        assertTrue(resultado.passageirosPendentes().isEmpty());
        assertTrue(agendamento.getIdVeiculoAberto() != null);
        assertEquals(tipoSelecionado, agendamento.getIdTipoVeiculo());
        verify(comandos).salvar(any(VeiculoAberto.class));
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
        when(consultas.buscarTipoVeiculo(tipoId))
                .thenReturn(new TipoVeiculoAgendaDados(tipoId, "Carro", 4, true));
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
    void separacaoAutomaticaAbreOutroCarroForaDaTolerancia() {
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
        when(consultas.buscarTipoVeiculo(tipoId))
                .thenReturn(new TipoVeiculoAgendaDados(tipoId, "Carro", 4, true));
        when(comandos.salvar(any(Agendamento.class))).thenAnswer(invocation ->
                invocation.getArgument(0, Agendamento.class).getId());
        when(comandos.salvar(any(VeiculoAberto.class))).thenAnswer(invocation ->
                invocation.getArgument(0, VeiculoAberto.class));

        var resultado = separarAutomaticamenteHandler.handler(
                new AgendamentoCommandSepararAutomaticamente(data));

        assertEquals(2, resultado.passageirosAlocados());
        assertTrue(resultado.passageirosPendentes().isEmpty());
        assertEquals(carro.id(), primeiro.getIdVeiculoAberto());
        assertTrue(segundo.getIdVeiculoAberto() != null);
        assertTrue(!carro.id().equals(segundo.getIdVeiculoAberto()));
        verify(comandos).salvar(any(VeiculoAberto.class));
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
