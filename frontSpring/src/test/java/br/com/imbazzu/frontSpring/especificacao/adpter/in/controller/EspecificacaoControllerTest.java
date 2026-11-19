package br.com.imbazzu.frontSpring.especificacao.adpter.in.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoRequestDto;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoResponseDto;
import br.com.imbazzu.frontSpring.especificacao.core.porta.in.EspecificacaoApplicationPorta;
import br.com.imbazzu.frontSpring.pessoa.core.dto.EnumPrioridade;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class EspecificacaoControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private EspecificacaoApplicationPorta application;

    @Test
    void carregaTabelaComPrioridade() throws Exception {
        var especificacao = new EspecificacaoResponseDto(UUID.randomUUID(), "Padrão",
                EnumPrioridade.MODERADA, null, null);
        when(application.listar("", false, 0))
                .thenReturn(new ResultadoPaginado<>(List.of(especificacao), 0, 10, 1, 1));

        mvc.perform(get("/especificacoes/listar").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Padrão")))
                .andExpect(content().string(containsString("Moderada")))
                .andExpect(content().string(containsString("badge text-bg-warning")));
    }

    @Test
    void paginaIniciaCarregamentoDaTabela() throws Exception {
        mvc.perform(get("/especificacoes"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hx-get=\"/especificacoes/listar\"")));
    }

    @Test
    void carregaTabelaDeArquivadasComPrioridade() throws Exception {
        var especificacao = new EspecificacaoResponseDto(UUID.randomUUID(), "Arquivada",
                EnumPrioridade.ALTA, null, LocalDate.of(2026, 1, 15));
        when(application.listar("", true, 0))
                .thenReturn(new ResultadoPaginado<>(List.of(especificacao), 0, 10, 1, 1));

        mvc.perform(get("/especificacoes/listar").param("arquivadas", "true").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Arquivada")))
                .andExpect(content().string(containsString("Alta")))
                .andExpect(content().string(containsString("badge text-bg-warning text-dark")))
                .andExpect(content().string(containsString("15/01/2026")));
    }

    @Test
    void carregaDetalhesComPrioridade() throws Exception {
        var id = UUID.randomUUID();
        when(application.buscar(id)).thenReturn(new EspecificacaoResponseDto(
                id, "Urgência", EnumPrioridade.URGENTE, null, null));

        mvc.perform(get("/especificacoes/{id}/detalhes", id).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Urgência")))
                .andExpect(content().string(containsString("Urgente")))
                .andExpect(content().string(containsString("badge text-bg-danger")));
    }

    @Test
    void abreFormularioDeCadastroComPrioridades() throws Exception {
        mvc.perform(get("/especificacoes/abrirFormulario").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hx-post=\"/especificacoes\"")))
                .andExpect(content().string(containsString("value=\"ALTA\"")))
                .andExpect(content().string(containsString("value=\"MODERADA\" selected=\"selected\"")))
                .andExpect(content().string(containsString("Alta")));
    }

    @Test
    void cadastraEspecificacaoComPrioridade() throws Exception {
        var dto = new EspecificacaoRequestDto("Transporte", EnumPrioridade.ALTA, "Observação");
        when(application.salvar(dto)).thenReturn(UUID.randomUUID());

        mvc.perform(post("/especificacoes").header("HX-Request", "true")
                        .param("nome", dto.nome()).param("prioridade", "ALTA").param("observacao", dto.observacao()))
                .andExpect(status().isOk())
                .andExpect(header().string("HX-Trigger-After-Settle", containsString("atualizar-especificacoes")));
        verify(application).salvar(dto);
    }

    @Test
    void recusaPrioridadeInvalidaSemErroNoServidor() throws Exception {
        mvc.perform(post("/especificacoes").header("HX-Request", "true")
                        .param("nome", "Transporte").param("prioridade", "Alta"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("is-invalid")));
    }

    @Test
    void abreFormularioDeEdicaoComPrioridadeSelecionada() throws Exception {
        var id = UUID.randomUUID();
        when(application.buscar(id)).thenReturn(new EspecificacaoResponseDto(
                id, "Transporte", EnumPrioridade.ALTA, "", null));

        mvc.perform(get("/especificacoes/abrirFormulario/{id}", id).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hx-put=\"/especificacoes/" + id + "\"")))
                .andExpect(content().string(containsString("value=\"ALTA\" selected=\"selected\"")));
    }

    @Test
    void editaPrioridadeDaEspecificacao() throws Exception {
        var id = UUID.randomUUID();
        var dto = new EspecificacaoRequestDto("Transporte", EnumPrioridade.BAIXA, "");
        mvc.perform(put("/especificacoes/{id}", id).header("HX-Request", "true")
                        .param("nome", dto.nome()).param("prioridade", "BAIXA").param("observacao", dto.observacao()))
                .andExpect(status().isOk());
        verify(application).editar(id, dto);
    }
}
