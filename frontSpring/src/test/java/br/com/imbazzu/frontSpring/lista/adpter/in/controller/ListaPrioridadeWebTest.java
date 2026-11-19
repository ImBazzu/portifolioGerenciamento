package br.com.imbazzu.frontSpring.lista.adpter.in.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    void edicaoMostraPrioridadeCadastradaESelecionadaPorId() throws Exception {
        var id = UUID.randomUUID();
        var prioridade = UUID.randomUUID();
        var regra = new ListaDetalheApiDto.RegraCadastro(UUID.randomUUID(), List.of(), List.of(),
                List.of(prioridade.toString()), null, null, false, null, List.of(), 1);
        when(listas.buscarAtiva(id)).thenReturn(new ListaResponseDto(
                id, "Lista", "", 1, 0, false, List.of(), List.of(), List.of(regra), List.of()));
        when(prioridades.listar()).thenReturn(List.of(new PrioridadeApi.Prioridade(prioridade, "Especial", true, 2, false)));
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
        when(prioridades.listar()).thenReturn(List.of(new PrioridadeApi.Prioridade(prioridade, "Arquivada", false, 0, true)));
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
