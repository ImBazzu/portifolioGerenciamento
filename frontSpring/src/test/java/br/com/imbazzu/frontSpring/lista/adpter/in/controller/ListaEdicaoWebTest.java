package br.com.imbazzu.frontSpring.lista.adpter.in.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaDetalheApiDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRegrasRequestDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRequestDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ListaEdicaoWebTest {
    private static final UUID ID = UUID.fromString("b4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f21");
    private static final UUID VEICULO = UUID.fromString("d4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f22");

    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private ListaApplicationPorta listas;
    @MockitoBean
    private LocalidadeApplicationPorta localidades;
    @MockitoBean
    private VeiculoApplicationPorta veiculos;

    @BeforeEach
    void configurar() {
        when(veiculos.listar("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new TipoVeiculoResponseDto(VEICULO, "Carro", 4)), 0, 10, 1, 1));
        when(localidades.listarCidades("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
    }

    @Test
    void formularioDeEdicaoMostraDiasEIntervaloExistentes() throws Exception {
        var regra = new ListaDetalheApiDto.RegraCadastro(UUID.randomUUID(),
                List.of(new ListaDetalheApiDto.VeiculoResponse(VEICULO, "Carro")), List.of(),
                List.of("ALTA"), null, null, false, null, List.of("MONDAY", "FRIDAY"), 3);
        when(listas.buscarAtiva(ID)).thenReturn(new ListaResponseDto(
                ID, "Lista Centro", "", 1, 0, false, List.of(), List.of(), List.of(regra), List.of()));

        mvc.perform(get("/listas/abrirFormulario/{id}", ID).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hx-put=\"/listas/" + ID + "\"")))
                .andExpect(content().string(containsString("value=\"MONDAY\" checked=\"checked\"")))
                .andExpect(content().string(containsString("value=\"FRIDAY\" checked=\"checked\"")))
                .andExpect(content().string(containsString("value=\"3\"")));
    }

    @Test
    void formularioAceitaRegraSemRestricaoDeDias() throws Exception {
        var regra = new ListaDetalheApiDto.RegraCadastro(UUID.randomUUID(),
                List.of(new ListaDetalheApiDto.VeiculoResponse(VEICULO, "Carro")), List.of(),
                List.of("ALTA"), null, null, false, null, List.of(), 1);
        when(listas.buscarAtiva(ID)).thenReturn(new ListaResponseDto(
                ID, "Lista Centro", "", 1, 0, false, List.of(), List.of(), List.of(regra), List.of()));

        mvc.perform(get("/listas/abrirFormulario/{id}", ID).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("value=\"MONDAY\"")))
                .andExpect(content().string(containsString("value=\"1\"")));
    }

    @Test
    void salvarEdicaoEncaminhaDiasEIntervalo() throws Exception {
        var regras = new ListaRegrasRequestDto(
                List.of(new ListaRegrasRequestDto.RegraCadastro(List.of(VEICULO), List.of(), List.of("ALTA"),
                        null, null, false, null, List.of("MONDAY", "FRIDAY"), 3)), List.of());
        String json = """
                {"regrasCadastro":[{"idsVeiculos":["%s"],"idsCidades":[],"prioridades":["ALTA"],
                "inicioHorario":null,"finalHorario":null,"obrigacaoAnexo":false,"descricao":null,
                "diasSemana":["MONDAY","FRIDAY"],"intervaloRepetacao":3}],"regrasSeparacao":[]}
                """.formatted(VEICULO);

        mvc.perform(put("/listas/{id}", ID).header("HX-Request", "true")
                        .param("nome", "Lista Centro").param("descricao", "").param("regrasJson", json))
                .andExpect(status().isOk())
                .andExpect(header().string("HX-Trigger-After-Settle", containsString("listar-ativos")));
        verify(listas).editar(ID, new ListaRequestDto("Lista Centro", ""));
        verify(listas).atualizarRegras(ID, regras);
    }
}
