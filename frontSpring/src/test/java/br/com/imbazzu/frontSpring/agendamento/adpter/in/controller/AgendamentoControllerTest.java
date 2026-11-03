package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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

import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificacaoVeiculosResponseDto;
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
        var passageiro = new AgendamentoResponseDto(
                UUID.randomUUID(), data, idPessoa, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                idDestino, LocalTime.of(8, 0), LocalTime.of(6, 0), false, 0, null, List.of());
        var agenda = new AgendaDiaResponseDto(data, List.of(), List.of(passageiro));
        when(api.buscarDia(data)).thenReturn(agenda);
        when(pessoas.buscar(idPessoa)).thenReturn(new PessoaResponseDto(
                idPessoa, "Robert", "", PessoaPrioridade.ALTA, null, "", "123", "11999999999"));
        when(listas.listarAtivas("", 0))
                .thenReturn(new ResultadoPaginado<ListaResponseDto>(List.of(), 0, 5, 0, 0));
        when(veiculos.listar("", 0))
                .thenReturn(new ResultadoPaginado<TipoVeiculoResponseDto>(List.of(), 0, 5, 0, 0));
        when(localidades.listarCidades("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new CidadeResponseDto(idCidade, "Aracaju", "SE", "00:30", 1)), 0, 5, 1, 1));
        when(localidades.listarDestinos(idCidade, "", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new DestinoResponseDto(idDestino, "Cirurgia", 10)), 0, 5, 1, 1));
        var model = new ExtendedModelMap();

        var view = controller.inicio(data, model);

        assertEquals("agendamento/inicio", view);
        assertEquals(
                new AgendamentoController.PassageiroDetalheView(
                        "Robert", "11999999999", "", "Aracaju", "Cirurgia"),
                ((java.util.Map<?, ?>) model.getAttribute("detalhesPassageiros")).get(idPessoa));
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
}
