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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificacaoVeiculosResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoPermitidoDto;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;

class AgendamentoControllerTest {

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
                data, idPessoa, idLista, idRegra, idTipo, idDestino,
                LocalTime.of(9, 0), true, 1, redirect);

        assertEquals("redirect:/agendamentos?data=2026-11-02", resultado);
        assertEquals(idPessoa, redirect.getFlashAttributes().get("idPessoaSelecionada"));
        assertEquals(idLista, redirect.getFlashAttributes().get("idListaSelecionada"));
        assertEquals(idDestino, redirect.getFlashAttributes().get("idDestinoSelecionado"));
        assertEquals(idTipo, redirect.getFlashAttributes().get("idTipoVeiculoSelecionado"));
        assertEquals(idRegra, redirect.getFlashAttributes().get("idRegraCadastroSelecionada"));
        assertEquals(true, redirect.getFlashAttributes().get("buscarNaResidenciaSelecionado"));
        assertSame(verificacao, redirect.getFlashAttributes().get("verificacao"));
        assertEquals("Agendamento inválido", redirect.getFlashAttributes().get("erro"));
    }
}
