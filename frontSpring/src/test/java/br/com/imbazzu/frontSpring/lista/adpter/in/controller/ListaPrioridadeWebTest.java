package br.com.imbazzu.frontSpring.lista.adpter.in.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaDetalheApiDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.prioridade.PrioridadeApi;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ListaPrioridadeWebTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private ListaApplicationPorta listas;
    @MockitoBean private LocalidadeApplicationPorta localidades;
    @MockitoBean private VeiculoApplicationPorta veiculos;
    @MockitoBean private PrioridadeApi prioridades;

    @Test
    void separaPrioridadesAtivasDasArquivadasNoModal() throws Exception {
        var ativa = new PrioridadeApi.Prioridade(UUID.randomUUID(), "Ativa exemplo", false, 0, false, 3);
        var arquivada = new PrioridadeApi.Prioridade(UUID.randomUUID(), "Arquivada exemplo", true, 2, true, 1);
        when(prioridades.listar()).thenReturn(List.of(ativa, arquivada));
        when(prioridades.buscar(arquivada.id())).thenReturn(arquivada);

        mvc.perform(get("/prioridades"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ativa exemplo")))
                .andExpect(content().string(not(containsString("Arquivada exemplo"))))
                .andExpect(content().string(containsString("hx-get=\"/prioridades/listarArquivadas\"")));
        mvc.perform(get("/prioridades/listarArquivadas").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Arquivada exemplo")))
                .andExpect(content().string(not(containsString("Ativa exemplo"))))
                .andExpect(content().string(containsString("/prioridades/" + arquivada.id() + "/desarquivar")))
                .andExpect(content().string(containsString("/prioridades/" + arquivada.id() + "/detalhes")));
        mvc.perform(get("/prioridades/{id}/detalhes", arquivada.id()).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hx-get=\"/prioridades/listarArquivadas\"")));
        mvc.perform(post("/prioridades/{id}/desarquivar", arquivada.id()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/prioridades"));
        verify(prioridades).alterarStatus(arquivada.id(), false);
    }

    @Test
    void apresentaEstadoVazioParaCadaLista() throws Exception {
        when(prioridades.listar()).thenReturn(List.of());
        mvc.perform(get("/prioridades"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nenhuma prioridade ativa encontrada.")));
        mvc.perform(get("/prioridades/listarArquivadas").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nenhuma prioridade arquivada encontrada.")));
    }

    @Test
    void tabelaExibeDadosEQuantidadeDeEspecificacoes() throws Exception {
        when(prioridades.listar()).thenReturn(List.of(
                new PrioridadeApi.Prioridade(UUID.randomUUID(), "Com busca", true, 2, false, 5),
                new PrioridadeApi.Prioridade(UUID.randomUUID(), "Sem busca", false, 0, false, 0)));
        mvc.perform(get("/prioridades"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<th>Busca na residência</th>")))
                .andExpect(content().string(containsString("<th>Especificações vinculadas</th>")))
                .andExpect(content().string(containsString("<td>Sim</td>")))
                .andExpect(content().string(containsString("<td>Não</td>")))
                .andExpect(content().string(containsString("<td>2</td>")))
                .andExpect(content().string(containsString("<td>5</td>")))
                .andExpect(content().string(containsString("<td>0</td>")))
                .andExpect(content().string(not(containsString("<th>Status</th>"))));
    }

    @Test
    void paginasDeCadastroRenderizamCabecalhosEAcoes() throws Exception {
        when(prioridades.listar()).thenReturn(List.of());
        mvc.perform(get("/listas"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h1>Listas</h1>")))
                .andExpect(content().string(containsString("hx-get=\"/listas/abrirFormulario\"")));
        mvc.perform(get("/localidade"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h1>Cidades e destinos</h1>")))
                .andExpect(content().string(containsString("hx-get=\"/localidade/abrirFormulario\"")));
        mvc.perform(get("/prioridades"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h1>Prioridades</h1>")))
                .andExpect(content().string(containsString("hx-get=\"/prioridades/abrirFormulario\"")));
    }

    @Test
    void formulariosCompartilhadosConservamTitulosEFechamento() throws Exception {
        mvc.perform(get("/localidade/abrirFormulario").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cadastrar cidade")))
                .andExpect(content().string(containsString("hx-get=\"/localidade/limpar-modal\"")));
        mvc.perform(get("/localidade/{id}/destinos/abrirFormulario", UUID.randomUUID())
                        .header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cadastrar destino")))
                .andExpect(content().string(containsString("hx-get=\"/localidade/limpar-modal\"")));
        mvc.perform(get("/prioridades/abrirFormulario").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cadastrar Prioridade")))
                .andExpect(content().string(containsString("hx-get=\"/prioridades/limpar-modal\"")));
        mvc.perform(get("/veiculo/abrirFormulario").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cadastrar Veículo")))
                .andExpect(content().string(containsString("hx-get=\"/veiculo/limpar-modal\"")));
    }

    @Test
    void edicaoMostraPrioridadeCadastradaESelecionadaPorId() throws Exception {
        var id = UUID.randomUUID();
        var prioridade = UUID.randomUUID();
        var regra = new ListaDetalheApiDto.RegraCadastro(UUID.randomUUID(), List.of(), List.of(),
                List.of(prioridade.toString()), null, null, false, null, List.of(), 1);
        when(listas.buscarAtiva(id)).thenReturn(new ListaResponseDto(
                id, "Lista", "", 1, 0, false, List.of(), List.of(), List.of(regra), List.of()));
        when(prioridades.listar()).thenReturn(List.of(new PrioridadeApi.Prioridade(prioridade, "Especial", true, 2, false, 0)));
        when(localidades.listarCidades("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 20, 0, 0));
        when(veiculos.listar("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 20, 0, 0));

        mvc.perform(get("/listas/abrirFormulario/{id}", id).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("value=\"" + prioridade + "\" checked=\"checked\"")))
                .andExpect(content().string(containsString("Especial")));
    }

    @Test
    void prioridadeArquivadaPermaneceNaRegraExistenteMasNaoEmNovaRegra() throws Exception {
        var id = UUID.randomUUID();
        var prioridade = UUID.randomUUID();
        var regra = new ListaDetalheApiDto.RegraCadastro(UUID.randomUUID(), List.of(), List.of(),
                List.of(prioridade.toString()), null, null, false, null, List.of(), 1);
        when(listas.buscarAtiva(id)).thenReturn(new ListaResponseDto(
                id, "Lista", "", 1, 0, false, List.of(), List.of(), List.of(regra), List.of()));
        when(prioridades.listar()).thenReturn(List.of(new PrioridadeApi.Prioridade(prioridade, "Arquivada", false, 0, true, 0)));
        when(localidades.listarCidades("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 20, 0, 0));
        when(veiculos.listar("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 20, 0, 0));
        mvc.perform(get("/listas/abrirFormulario/{id}", id).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Arquivada (arquivada)")))
                .andExpect(content().string(containsString("value=\"" + prioridade + "\" checked=\"checked\"")));
        mvc.perform(get("/listas/abrirFormulario").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("value=\"" + prioridade + "\""))));
    }
}
