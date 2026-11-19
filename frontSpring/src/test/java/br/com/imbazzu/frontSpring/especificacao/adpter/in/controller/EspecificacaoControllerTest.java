package br.com.imbazzu.frontSpring.especificacao.adpter.in.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoRequestDto;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoResponseDto;
import br.com.imbazzu.frontSpring.especificacao.core.porta.in.EspecificacaoApplicationPorta;
import br.com.imbazzu.frontSpring.prioridade.PrioridadeApi;
import org.junit.jupiter.api.BeforeEach;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class EspecificacaoControllerTest {
    private static final UUID PRIORIDADE_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private EspecificacaoApplicationPorta application;

    @MockitoBean
    private PrioridadeApi prioridades;

    @BeforeEach
    void configurarPrioridades() {
        when(prioridades.listar()).thenReturn(List.of(new PrioridadeApi.Prioridade(PRIORIDADE_ID, "Alta", true, 2, false, 0)));
    }

    @Test
    void carregaTabelaComPrioridade() throws Exception {
        var especificacao = new EspecificacaoResponseDto(UUID.randomUUID(), "Padrão",
                PRIORIDADE_ID, "Moderada", null, null);
        when(application.listar("", false, 0))
                .thenReturn(new ResultadoPaginado<>(List.of(especificacao), 0, 10, 1, 1));

        mvc.perform(get("/especificacoes/listar").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Padrão")))
                .andExpect(content().string(containsString("Moderada")))
                .andExpect(content().string(containsString("badge text-bg-secondary")));
    }

    @Test
    void paginaIniciaCarregamentoDaTabela() throws Exception {
        mvc.perform(get("/especificacoes"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h1>Especificações</h1>")))
                .andExpect(content().string(containsString("hx-get=\"/especificacoes/abrirFormulario\"")))
                .andExpect(content().string(matchesPattern(
                        "(?s).*hx-get=\"/especificacoes/listar\\?arquivadas=true\"\\s+hx-target=\"#modal\".*")))
                .andExpect(content().string(containsString("hx-get=\"/especificacoes/listar\"")));
    }

    @Test
    void carregaTabelaDeArquivadasComPrioridade() throws Exception {
        var especificacao = new EspecificacaoResponseDto(UUID.randomUUID(), "Arquivada",
                PRIORIDADE_ID, "Alta", null, LocalDate.of(2026, 1, 15));
        when(application.listar("Arq", true, 1))
                .thenReturn(new ResultadoPaginado<>(List.of(especificacao), 1, 10, 30, 3));

        mvc.perform(get("/especificacoes/listar").param("arquivadas", "true").param("nome", "Arq")
                        .param("pagina", "1").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("aria-labelledby=\"especificacoesArquivadasTitulo\"")))
                .andExpect(content().string(containsString("atualizar-especificacoes from:body")))
                .andExpect(content().string(containsString("nome=Arq&amp;arquivadas=true&amp;pagina=2")))
                .andExpect(content().string(containsString("hx-target=\"#modal\"")))
                .andExpect(content().string(containsString("/especificacoes/" + especificacao.id() + "/desarquivar")))
                .andExpect(content().string(containsString("Arquivada")))
                .andExpect(content().string(containsString("Alta")))
                .andExpect(content().string(containsString("badge text-bg-secondary")))
                .andExpect(content().string(containsString("15/01/2026")));
        mvc.perform(patch("/especificacoes/{id}/desarquivar", especificacao.id()).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(header().string("HX-Trigger-After-Settle", "atualizar-especificacoes"));
        verify(application).desarquivar(especificacao.id());
    }

    @Test
    void carregaDetalhesComPrioridade() throws Exception {
        var id = UUID.randomUUID();
        when(application.buscar(id)).thenReturn(new EspecificacaoResponseDto(
                id, "Urgência", PRIORIDADE_ID, "Urgente", null, null));

        mvc.perform(get("/especificacoes/{id}/detalhes", id).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Urgência")))
                .andExpect(content().string(containsString("Urgente")))
                .andExpect(content().string(containsString("badge text-bg-secondary")))
                .andExpect(content().string(containsString("/especificacoes/" + id + "/arquivar")))
                .andExpect(content().string(not(containsString("hx-delete="))))
                .andExpect(content().string(not(containsString("/desarquivar"))));
    }

    @Test
    void detalhesArquivadosPermitemDesarquivarEExcluir() throws Exception {
        var id = UUID.randomUUID();
        when(application.buscarArquivado(id)).thenReturn(new EspecificacaoResponseDto(
                id, "Transporte", PRIORIDADE_ID, "Alta", "Observação", LocalDate.of(2026, 1, 15)));
        mvc.perform(get("/especificacoes/{id}/detalhes/arquivado", id).param("nome", "Transporte")
                        .param("pagina", "1").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Observação")))
                .andExpect(content().string(containsString("/especificacoes/" + id + "/desarquivar")))
                .andExpect(content().string(containsString("hx-delete=\"/especificacoes/" + id + "?nome=Transporte&amp;pagina=1\"")))
                .andExpect(content().string(containsString("/especificacoes/listar?arquivadas=true&amp;nome=Transporte&amp;pagina=1")))
                .andExpect(content().string(not(containsString("/" + id + "/arquivar"))));
        mvc.perform(delete("/especificacoes/{id}", id).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(header().string("HX-Trigger-After-Settle", "atualizar-especificacoes"));
        verify(application).excluir(id);
    }

    @Test
    void exibeErroAoExcluirEspecificacaoVinculada() throws Exception {
        var id = UUID.randomUUID();
        when(application.buscarArquivado(id)).thenReturn(new EspecificacaoResponseDto(
                id, "Transporte", PRIORIDADE_ID, "Alta", "", LocalDate.of(2026, 1, 15)));
        doThrow(HttpClientErrorException.create(HttpStatus.CONFLICT, "Conflict", null,
                "{\"message\":\"Especificação vinculada\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8),
                java.nio.charset.StandardCharsets.UTF_8)).when(application).excluir(id);
        mvc.perform(delete("/especificacoes/{id}", id).param("nome", "Transporte").param("pagina", "1")
                        .header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Especificação vinculada")))
                .andExpect(content().string(containsString("/especificacoes/listar?arquivadas=true&amp;nome=Transporte&amp;pagina=1")))
                .andExpect(header().doesNotExist("HX-Trigger-After-Settle"));
    }

    @Test
    void abreFormularioDeCadastroComPrioridades() throws Exception {
        mvc.perform(get("/especificacoes/abrirFormulario").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hx-post=\"/especificacoes\"")))
                .andExpect(content().string(containsString("value=\"" + PRIORIDADE_ID + "\"")))
                .andExpect(content().string(containsString("Alta")));
    }

    @Test
    void paginaPermiteCadastrarEEditarPrioridades() throws Exception {
        mvc.perform(get("/prioridades"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Alta")))
                .andExpect(content().string(containsString("hx-get=\"/prioridades/abrirFormulario\"")))
                .andExpect(content().string(not(containsString("name=\"quantidadeAcompanhantes\""))))
                .andExpect(content().string(containsString("/prioridades/" + PRIORIDADE_ID + "/detalhes")));

        mvc.perform(get("/prioridades/abrirFormulario").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cadastrar Prioridade")))
                .andExpect(content().string(containsString("action=\"/prioridades\"")))
                .andExpect(content().string(containsString("name=\"quantidadeAcompanhantes\"")))
                .andExpect(content().string(containsString("name=\"buscarNaResidencia\"")))
                .andExpect(content().string(containsString("hx-get=\"/prioridades/limpar-modal\"")));

        mvc.perform(post("/prioridades").param("nome", "Especial")
                        .param("buscarNaResidencia", "true").param("quantidadeAcompanhantes", "2"))
                .andExpect(status().is3xxRedirection());
        verify(prioridades).criar(new PrioridadeApi.Dados("Especial", true, 2));

        mvc.perform(post("/prioridades/{id}", PRIORIDADE_ID).param("nome", "Alta")
                        .param("quantidadeAcompanhantes", "1"))
                .andExpect(status().is3xxRedirection());
        verify(prioridades).editar(PRIORIDADE_ID, new PrioridadeApi.Dados("Alta", false, 1));
    }

    @Test
    void paginaPermiteArquivarReativarEExcluir() throws Exception {
        var arquivada = UUID.randomUUID();
        when(prioridades.listar()).thenReturn(List.of(
                new PrioridadeApi.Prioridade(PRIORIDADE_ID, "Ativa", false, 0, false, 0),
                new PrioridadeApi.Prioridade(arquivada, "Arquivada", false, 0, true, 0)));
        when(prioridades.buscar(PRIORIDADE_ID)).thenReturn(new PrioridadeApi.Prioridade(PRIORIDADE_ID, "Ativa", true, 2, false, 0));
        when(prioridades.buscar(arquivada)).thenReturn(new PrioridadeApi.Prioridade(arquivada, "Arquivada", false, 0, true, 0));
        mvc.perform(get("/prioridades"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/prioridades/" + PRIORIDADE_ID + "/detalhes")))
                .andExpect(content().string(not(containsString("/prioridades/" + arquivada + "/detalhes"))))
                .andExpect(content().string(not(containsString("/excluir"))));
        mvc.perform(get("/prioridades/{id}/detalhes", PRIORIDADE_ID).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/prioridades/" + PRIORIDADE_ID + "/arquivar")))
                .andExpect(content().string(containsString("Sim")))
                .andExpect(content().string(not(containsString("/excluir"))))
                .andExpect(content().string(not(containsString("/desarquivar"))));
        mvc.perform(get("/prioridades/{id}/detalhes", arquivada).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/prioridades/" + arquivada + "/desarquivar")))
                .andExpect(content().string(not(containsString("/" + arquivada + "/arquivar"))))
                .andExpect(content().string(containsString("/prioridades/" + arquivada + "/excluir")));
        mvc.perform(post("/prioridades/{id}/arquivar", PRIORIDADE_ID)).andExpect(status().is3xxRedirection());
        mvc.perform(post("/prioridades/{id}/desarquivar", arquivada)).andExpect(status().is3xxRedirection());
        mvc.perform(post("/prioridades/{id}/excluir", arquivada)).andExpect(status().is3xxRedirection());
        verify(prioridades).alterarStatus(PRIORIDADE_ID, true);
        verify(prioridades).alterarStatus(arquivada, false);
        verify(prioridades).excluir(arquivada);
    }

    @Test
    void prioridadeArquivadaApareceSomenteQuandoJaVinculada() throws Exception {
        var id = UUID.randomUUID();
        when(prioridades.listar()).thenReturn(List.of(new PrioridadeApi.Prioridade(PRIORIDADE_ID, "Alta", true, 2, true, 0)));
        when(application.buscar(id)).thenReturn(new EspecificacaoResponseDto(
                id, "Transporte", PRIORIDADE_ID, "Alta", "", null));
        mvc.perform(get("/especificacoes/abrirFormulario").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("value=\"" + PRIORIDADE_ID + "\""))));
        mvc.perform(get("/especificacoes/abrirFormulario/{id}", id).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Alta (arquivada)")))
                .andExpect(content().string(containsString("value=\"" + PRIORIDADE_ID + "\" selected=\"selected\"")));
    }

    @Test
    void cadastraEspecificacaoComPrioridade() throws Exception {
        var dto = new EspecificacaoRequestDto("Transporte", PRIORIDADE_ID, "Observação");
        when(application.salvar(dto)).thenReturn(UUID.randomUUID());

        mvc.perform(post("/especificacoes").header("HX-Request", "true")
                        .param("nome", dto.nome()).param("prioridade", PRIORIDADE_ID.toString()).param("observacao", dto.observacao()))
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
                id, "Transporte", PRIORIDADE_ID, "Alta", "", null));

        mvc.perform(get("/especificacoes/abrirFormulario/{id}", id).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hx-put=\"/especificacoes/" + id + "\"")))
                .andExpect(content().string(containsString("value=\"" + PRIORIDADE_ID + "\" selected=\"selected\"")));
    }

    @Test
    void editaPrioridadeDaEspecificacao() throws Exception {
        var id = UUID.randomUUID();
        var dto = new EspecificacaoRequestDto("Transporte", PRIORIDADE_ID, "");
        mvc.perform(put("/especificacoes/{id}", id).header("HX-Request", "true")
                        .param("nome", dto.nome()).param("prioridade", PRIORIDADE_ID.toString()).param("observacao", dto.observacao()))
                .andExpect(status().isOk());
        verify(application).editar(id, dto);
    }
}
