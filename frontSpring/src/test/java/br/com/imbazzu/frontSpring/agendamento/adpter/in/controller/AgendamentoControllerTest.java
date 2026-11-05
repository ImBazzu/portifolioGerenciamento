package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificacaoVeiculosResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoPermitidoDto;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.DestinoResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaPrioridade;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;

class AgendamentoControllerTest {

    @Test
    void naoPermiteAbrirCarroPelaAgendaGeral() {
        var api = mock(AgendamentoApi.class);
        var veiculos = mock(VeiculoApplicationPorta.class);
        var controller = criarController(api, veiculos);
        var data = LocalDate.of(2026, 11, 2);
        var idTipo = UUID.randomUUID();
        when(veiculos.buscar(idTipo)).thenReturn(new TipoVeiculoResponseDto(idTipo, "Carro", 4));
        var redirect = new RedirectAttributesModelMap();

        var resultado = controller.abrirVeiculo(data, idTipo, "Carro 1", null, redirect);

        assertEquals("redirect:/agendamentos?data=2026-11-02", resultado);
        assertTrue(redirect.getFlashAttributes().get("erro").toString().contains("só podem ser abertos"));
        verify(api, never()).abrirVeiculo(any(), any());
    }

    @Test
    void permiteAbrirCarroNaPaginaDePassageirosDeCarros() {
        var api = mock(AgendamentoApi.class);
        var veiculos = mock(VeiculoApplicationPorta.class);
        var controller = criarController(api, veiculos);
        var data = LocalDate.of(2026, 11, 2);
        var idTipo = UUID.randomUUID();
        when(veiculos.buscar(idTipo)).thenReturn(new TipoVeiculoResponseDto(idTipo, "Carro", 4));
        var redirect = new RedirectAttributesModelMap();

        var resultado = controller.abrirCarro(data, idTipo, "Carro 1", redirect);

        assertEquals("redirect:/agendamentos/carros?data=2026-11-02", resultado);
        assertEquals("Veículo aberto para a agenda do dia.", redirect.getFlashAttributes().get("sucesso"));
        verify(api).abrirVeiculo(eq(data), any());
    }

    @Test
    void naoPermiteAbrirVanNaPaginaDePassageirosDeCarros() {
        var api = mock(AgendamentoApi.class);
        var veiculos = mock(VeiculoApplicationPorta.class);
        var controller = criarController(api, veiculos);
        var data = LocalDate.of(2026, 11, 2);
        var idTipo = UUID.randomUUID();
        when(veiculos.buscar(idTipo)).thenReturn(new TipoVeiculoResponseDto(idTipo, "Van", 15));
        var redirect = new RedirectAttributesModelMap();

        var resultado = controller.abrirCarro(data, idTipo, "Van 1", redirect);

        assertEquals("redirect:/agendamentos/carros?data=2026-11-02", resultado);
        assertTrue(redirect.getFlashAttributes().get("erro").toString().contains("Selecione um tipo"));
        verify(api, never()).abrirVeiculo(any(), any());
    }

    @Test
    void permiteAbrirVanPelaAgendaGeral() {
        var api = mock(AgendamentoApi.class);
        var veiculos = mock(VeiculoApplicationPorta.class);
        var controller = criarController(api, veiculos);
        var data = LocalDate.of(2026, 11, 2);
        var idTipo = UUID.randomUUID();
        when(veiculos.buscar(idTipo)).thenReturn(new TipoVeiculoResponseDto(idTipo, "Van", 15));
        var redirect = new RedirectAttributesModelMap();

        var resultado = controller.abrirVeiculo(
                data, idTipo, "Van 1", LocalTime.of(8, 0), redirect);

        assertEquals("redirect:/agendamentos?data=2026-11-02", resultado);
        assertEquals("Veículo aberto para a agenda do dia.", redirect.getFlashAttributes().get("sucesso"));
        verify(api).abrirVeiculo(eq(data), any());
    }

    @Test
    void editaVeiculoAbertoERetornaParaAgendaGeral() {
        var api = mock(AgendamentoApi.class);
        var controller = criarController(api, mock(VeiculoApplicationPorta.class));
        var idVeiculo = UUID.randomUUID();
        var data = LocalDate.of(2026, 11, 2);
        var redirect = new RedirectAttributesModelMap();

        var resultado = controller.editarVeiculo(
                idVeiculo, data, "Van 02", LocalTime.of(8, 30), false, redirect);

        assertEquals("redirect:/agendamentos?data=2026-11-02", resultado);
        assertEquals("Veículo atualizado.", redirect.getFlashAttributes().get("sucesso"));
        verify(api).atualizarVeiculo(eq(idVeiculo), any());
    }

    @Test
    void excluiVeiculoDeCarrosERetornaParaPaginaDeCarros() {
        var api = mock(AgendamentoApi.class);
        var controller = criarController(api, mock(VeiculoApplicationPorta.class));
        var idVeiculo = UUID.randomUUID();
        var data = LocalDate.of(2026, 11, 2);
        var redirect = new RedirectAttributesModelMap();

        var resultado = controller.excluirVeiculo(idVeiculo, data, true, redirect);

        assertEquals("redirect:/agendamentos/carros?data=2026-11-02", resultado);
        assertTrue(redirect.getFlashAttributes().get("sucesso").toString().contains("passageiros"));
        verify(api).removerVeiculo(idVeiculo);
    }

    @Test
    void excluiPassageiroPendenteDeCarroERetornaParaPaginaDeCarros() {
        var api = mock(AgendamentoApi.class);
        var controller = criarController(api, mock(VeiculoApplicationPorta.class));
        var idAgendamento = UUID.randomUUID();
        var data = LocalDate.of(2026, 11, 2);
        var redirect = new RedirectAttributesModelMap();

        var resultado = controller.excluir(idAgendamento, data, true, redirect);

        assertEquals("redirect:/agendamentos/carros?data=2026-11-02", resultado);
        assertEquals("Agendamento excluído.", redirect.getFlashAttributes().get("sucesso"));
        verify(api).remover(idAgendamento);
    }

    @Test
    void editaAgendamentoERetornaParaPaginaDeCarros() {
        var api = mock(AgendamentoApi.class);
        var controller = criarController(api, mock(VeiculoApplicationPorta.class));
        var idAgendamento = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idVeiculo = UUID.randomUUID();
        var data = LocalDate.of(2026, 11, 2);
        var redirect = new RedirectAttributesModelMap();

        var resultado = controller.editar(
                idAgendamento, data, idTipo, idDestino, LocalTime.of(10, 0), true, 2,
                idVeiculo, true, redirect);

        assertEquals("redirect:/agendamentos/carros?data=2026-11-02", resultado);
        assertEquals("Agendamento atualizado.", redirect.getFlashAttributes().get("sucesso"));
        verify(api).atualizar(eq(idAgendamento), any());
    }

    @Test
    void naoPermiteEditarAgendamentoDeCarroPelaAgendaGeral() {
        var api = mock(AgendamentoApi.class);
        var veiculos = mock(VeiculoApplicationPorta.class);
        var controller = criarController(api, veiculos);
        var idAgendamento = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var data = LocalDate.of(2026, 11, 2);
        when(veiculos.buscar(idTipo)).thenReturn(new TipoVeiculoResponseDto(idTipo, "Carro", 4));
        var redirect = new RedirectAttributesModelMap();

        var resultado = controller.editar(
                idAgendamento, data, idTipo, UUID.randomUUID(), LocalTime.of(10, 0), false, 0,
                null, false, redirect);

        assertEquals("redirect:/agendamentos?data=2026-11-02", resultado);
        assertTrue(redirect.getFlashAttributes().get("erro").toString().contains("página de passageiros de carros"));
        verify(api, never()).atualizar(eq(idAgendamento), any());
    }

    @Test
    void pdfDeVeiculoNaoCarroIncluiSomenteOsPassageirosDaqueleVeiculo() throws java.io.IOException {
        var api = mock(AgendamentoApi.class);
        var pessoas = mock(PessoaApplicationPorta.class);
        var listas = mock(ListaApplicationPorta.class);
        var veiculos = mock(VeiculoApplicationPorta.class);
        var localidades = mock(LocalidadeApplicationPorta.class);
        var controller = new AgendamentoController(
                api, pessoas, listas, veiculos, localidades, new ObjectMapper().findAndRegisterModules());
        var data = LocalDate.of(2026, 11, 2);
        var idVan = UUID.randomUUID();
        var idOnibus = UUID.randomUUID();
        var idPessoaVan = UUID.randomUUID();
        var idPessoaOnibus = UUID.randomUUID();
        var idTipoVan = UUID.randomUUID();
        var idTipoOnibus = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var passageiroVan = new AgendamentoResponseDto(
                UUID.randomUUID(), data, idPessoaVan, UUID.randomUUID(), UUID.randomUUID(), idTipoVan,
                idDestino, LocalTime.of(8, 0), LocalTime.of(6, 0), false, 1, idVan, List.of());
        var passageiroOnibus = new AgendamentoResponseDto(
                UUID.randomUUID(), data, idPessoaOnibus, UUID.randomUUID(), UUID.randomUUID(), idTipoOnibus,
                idDestino, LocalTime.of(9, 0), LocalTime.of(7, 0), false, 0, idOnibus, List.of());
        var van = new VeiculoAbertoResponseDto(
                idVan, data, idTipoVan, "Van", 15, "Van 1", LocalTime.of(6, 0), 2, List.of(passageiroVan));
        var onibus = new VeiculoAbertoResponseDto(
                idOnibus, data, idTipoOnibus, "Ônibus", 40, "Ônibus 1", LocalTime.of(7, 0), 1,
                List.of(passageiroOnibus));
        when(api.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(van, onibus), List.of()));
        when(pessoas.buscar(idPessoaVan)).thenReturn(new PessoaResponseDto(
                idPessoaVan, "Passageiro da van", "", PessoaPrioridade.ALTA, null, "", "123", "11999999999"));
        when(listas.listarAtivas("", 0))
                .thenReturn(new ResultadoPaginado<ListaResponseDto>(List.of(), 0, 5, 0, 0));
        when(veiculos.listar("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(
                        new TipoVeiculoResponseDto(idTipoVan, "Van", 15),
                        new TipoVeiculoResponseDto(idTipoOnibus, "Ônibus", 40)),
                0, 5, 2, 1));
        when(localidades.listarCidades("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new CidadeResponseDto(idCidade, "Aracaju", "SE", "00:30", 1)), 0, 5, 1, 1));
        when(localidades.listarDestinos(idCidade, "", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new DestinoResponseDto(idDestino, "Cirurgia", 10)), 0, 5, 1, 1));

        var resposta = controller.pdfVeiculo(idVan, data, new ExtendedModelMap());

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        var leitor = new PdfReader(resposta.getBody());
        var texto = new PdfTextExtractor(leitor).getTextFromPage(1);
        assertTrue(texto.contains("Passageiro da van"));
        assertFalse(texto.contains("Passageiro do ônibus"));
        leitor.close();
    }

    @Test
    void pdfGeralDeCarrosOrdenaVeiculosPorHorarioDeSaida() throws java.io.IOException {
        var api = mock(AgendamentoApi.class);
        var listas = mock(ListaApplicationPorta.class);
        var veiculos = mock(VeiculoApplicationPorta.class);
        var localidades = mock(LocalidadeApplicationPorta.class);
        var controller = new AgendamentoController(
                api,
                mock(PessoaApplicationPorta.class),
                listas,
                veiculos,
                localidades,
                new ObjectMapper().findAndRegisterModules());
        var data = LocalDate.of(2026, 11, 2);
        var carroTarde = new VeiculoAbertoResponseDto(
                UUID.randomUUID(), data, UUID.randomUUID(), "Carro", 4, "Carro tarde",
                LocalTime.of(9, 0), 0, List.of());
        var carroCedo = new VeiculoAbertoResponseDto(
                UUID.randomUUID(), data, UUID.randomUUID(), "Carro", 4, "Carro cedo",
                LocalTime.of(6, 30), 0, List.of());
        var carroSemHorario = new VeiculoAbertoResponseDto(
                UUID.randomUUID(), data, UUID.randomUUID(), "Carro", 4, "Carro sem horario",
                null, 0, List.of());
        when(api.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(
                data, List.of(carroTarde, carroSemHorario, carroCedo), List.of()));
        when(listas.listarAtivas("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(), 0, 5, 0, 0));
        when(veiculos.listar("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(), 0, 5, 0, 0));
        when(localidades.listarCidades("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(), 0, 5, 0, 0));

        var resposta = controller.pdfGeralCarros(data, new ExtendedModelMap());

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        var leitor = new PdfReader(resposta.getBody());
        var texto = new PdfTextExtractor(leitor).getTextFromPage(1);
        assertTrue(texto.indexOf("Carro cedo") < texto.indexOf("Carro tarde"));
        assertTrue(texto.indexOf("Carro tarde") < texto.indexOf("Carro sem horario"));
        leitor.close();
    }

    @Test
    void inicioCarregaDetalhesDosPassageirosDaAgenda() {
        var api = mock(AgendamentoApi.class);
        var pessoas = mock(PessoaApplicationPorta.class);
        var listas = mock(ListaApplicationPorta.class);
        var veiculos = mock(VeiculoApplicationPorta.class);
        var localidades = mock(LocalidadeApplicationPorta.class);
        var controller = new AgendamentoController(
                api, pessoas, listas, veiculos, localidades, new ObjectMapper().findAndRegisterModules());
        var data = LocalDate.of(2026, 11, 2);
        var idPessoa = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idCidade = UUID.randomUUID();
        var idTipoCarro = UUID.randomUUID();
        var idTipoVan = UUID.randomUUID();
        var idVanAberta = UUID.randomUUID();
        var passageiro = new AgendamentoResponseDto(
                UUID.randomUUID(), data, idPessoa, UUID.randomUUID(), UUID.randomUUID(), idTipoCarro,
                idDestino, LocalTime.of(8, 0), LocalTime.of(6, 0), false, 0, null, List.of());
        var vanDisponivel = new VeiculoAbertoResponseDto(
                idVanAberta, data, idTipoVan, "Van", 15, "Van 1", LocalTime.of(7, 0), 0, List.of());
        var agenda = new AgendaDiaResponseDto(data, List.of(vanDisponivel), List.of(passageiro));
        when(api.buscarDia(data)).thenReturn(agenda);
        when(pessoas.buscar(idPessoa)).thenReturn(new PessoaResponseDto(
                idPessoa, "Robert", "", PessoaPrioridade.ALTA, null, "", "123", "11999999999"));
        when(listas.listarAtivas("", 0))
                .thenReturn(new ResultadoPaginado<ListaResponseDto>(List.of(), 0, 5, 0, 0));
        when(veiculos.listar("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(
                        new TipoVeiculoResponseDto(idTipoCarro, "Carro", 4),
                        new TipoVeiculoResponseDto(idTipoVan, "Van", 15)),
                0, 5, 2, 1));
        when(localidades.listarCidades("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new CidadeResponseDto(idCidade, "Aracaju", "SE", "00:30", 1)), 0, 5, 1, 1));
        when(localidades.listarDestinos(idCidade, "", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new DestinoResponseDto(idDestino, "Cirurgia", 10)), 0, 5, 1, 1));
        var model = new ExtendedModelMap();
        model.addAttribute("idDestinoSelecionado", idDestino);

        var view = controller.inicio(data, model);

        assertEquals("agendamento/inicio", view);
        assertEquals(
                new AgendamentoController.PassageiroDetalheView(
                        "Robert", "11999999999", "", "Aracaju", "Cirurgia"),
                ((java.util.Map<?, ?>) model.getAttribute("detalhesPassageiros")).get(idPessoa));
        assertEquals(idCidade, model.getAttribute("idCidadeSelecionada"));
        assertEquals("Aracaju - SE", model.getAttribute("cidadeSelecionada"));
        assertEquals("Cirurgia", model.getAttribute("destinoSelecionado"));
        assertEquals(List.of(new TipoVeiculoResponseDto(idTipoCarro, "Carro", 4)),
                model.getAttribute("tiposVeiculoCarro"));
        assertEquals(List.of(new TipoVeiculoResponseDto(idTipoVan, "Van", 15)),
                model.getAttribute("tiposVeiculoNaoCarro"));
        var veiculosDisponiveis = (java.util.Map<?, ?>)
                model.getAttribute("veiculosDisponiveisParaAtribuicaoManual");
        assertEquals(List.of(idVanAberta), veiculosDisponiveis.get(passageiro.id()));
    }

    @Test
    void verificacaoHtmxRetornaSomenteResultadoSemRedirecionar() {
        var api = mock(AgendamentoApi.class);
        var pessoas = mock(PessoaApplicationPorta.class);
        var listas = mock(ListaApplicationPorta.class);
        var veiculos = mock(VeiculoApplicationPorta.class);
        var localidades = mock(LocalidadeApplicationPorta.class);
        var controller = new AgendamentoController(
                api,
                pessoas,
                listas,
                veiculos,
                localidades,
                new ObjectMapper().findAndRegisterModules());
        var data = LocalDate.of(2026, 11, 2);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        when(pessoas.buscar(idPessoa)).thenReturn(new PessoaResponseDto(
                idPessoa, "Robert", "", PessoaPrioridade.ALTA, null, "", "123", "11999999999"));
        var resposta = new VerificacaoVeiculosResponseDto(List.of(), List.of("Sem regra compatível"));
        var paginaVaziaListas = new ResultadoPaginado<ListaResponseDto>(List.of(), 0, 5, 0, 0);
        var paginaVaziaVeiculos = new ResultadoPaginado<TipoVeiculoResponseDto>(List.of(), 0, 5, 0, 0);
        var paginaVaziaCidades = new ResultadoPaginado<CidadeResponseDto>(List.of(), 0, 5, 0, 0);
        when(api.verificarVeiculos(eq(data), any())).thenReturn(resposta);
        when(api.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(), List.of()));
        when(listas.listarAtivas("", 0)).thenReturn(paginaVaziaListas);
        when(veiculos.listar("", 0)).thenReturn(paginaVaziaVeiculos);
        when(localidades.listarCidades("", 0)).thenReturn(paginaVaziaCidades);
        var model = new ExtendedModelMap();

        var view = controller.verificarVeiculos(
                data, idPessoa, idLista, idDestino, LocalTime.of(9, 0),
                0, false, null, "true", model, new RedirectAttributesModelMap());

        assertEquals("agendamento/inicio :: resultadoVerificacao", view);
        assertSame(resposta, model.getAttribute("verificacao"));
        assertEquals(idPessoa, model.getAttribute("idPessoaSelecionada"));
    }

    @Test
    void falhaAoCriarMantemDadosEAtualizaTiposPermitidos() {
        var api = mock(AgendamentoApi.class);
        var controller = new AgendamentoController(
                api,
                mock(PessoaApplicationPorta.class),
                mock(ListaApplicationPorta.class),
                mock(VeiculoApplicationPorta.class),
                mock(LocalidadeApplicationPorta.class),
                new ObjectMapper().findAndRegisterModules());
        var data = LocalDate.of(2026, 11, 2);
        var idPessoa = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idRegra = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var verificacao = new VerificacaoVeiculosResponseDto(
                List.of(new VeiculoPermitidoDto(idTipo, "Carro baixo", 4, List.of(idRegra))),
                List.of());
        var redirect = new RedirectAttributesModelMap();
        when(api.verificarVeiculos(eq(data), any())).thenReturn(verificacao);
        org.mockito.Mockito.doThrow(new HttpClientErrorException(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                "{\"mensagem\":\"Agendamento inválido\"}".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8))
                .when(api)
                .criar(eq(data), any());

        var resultado = controller.criar(
                data, idPessoa, idLista, idTipo, idDestino,
                LocalTime.of(9, 0), true, 1, null, redirect);

        assertEquals("redirect:/agendamentos?data=2026-11-02", resultado);
        assertEquals(idPessoa, redirect.getFlashAttributes().get("idPessoaSelecionada"));
        assertEquals(idLista, redirect.getFlashAttributes().get("idListaSelecionada"));
        assertEquals(idDestino, redirect.getFlashAttributes().get("idDestinoSelecionado"));
        assertEquals(idTipo, redirect.getFlashAttributes().get("idTipoVeiculoSelecionado"));
        assertEquals(true, redirect.getFlashAttributes().get("buscarNaResidenciaSelecionado"));
        assertSame(verificacao, redirect.getFlashAttributes().get("verificacao"));
        assertEquals("Agendamento inválido", redirect.getFlashAttributes().get("erro"));
    }

    private AgendamentoController criarController(AgendamentoApi api, VeiculoApplicationPorta veiculos) {
        return new AgendamentoController(
                api,
                mock(PessoaApplicationPorta.class),
                mock(ListaApplicationPorta.class),
                veiculos,
                mock(LocalidadeApplicationPorta.class),
                new ObjectMapper().findAndRegisterModules());
    }
}
