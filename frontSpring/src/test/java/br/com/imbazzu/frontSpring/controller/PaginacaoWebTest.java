package br.com.imbazzu.frontSpring.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.especificacao.core.porta.in.EspecificacaoApplicationPorta;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PaginacaoWebTest {
    private static final UUID CIDADE = UUID.fromString("00000000-0000-0000-0000-000000000123");
    private static final String BUSCA = "__PAGINA__";
    @Autowired private MockMvc mvc;
    @MockitoBean private LocalidadeApplicationPorta localidades;
    @MockitoBean private ListaApplicationPorta listas;
    @MockitoBean private VeiculoApplicationPorta veiculos;
    @MockitoBean private EspecificacaoApplicationPorta especificacoes;

    @BeforeEach
    void prepararPaginas() {
        when(localidades.listarCidades(BUSCA, 3)).thenReturn(pagina(3, 100));
        when(localidades.listarCidadesArquivadas(BUSCA, 3)).thenReturn(pagina(3, 100));
        when(localidades.listarDestinos(CIDADE, BUSCA, 3)).thenReturn(pagina(3, 100));
        when(localidades.listarDestinosArquivados(CIDADE, BUSCA, 3)).thenReturn(pagina(3, 100));
        when(listas.listarAtivas(BUSCA, 3)).thenReturn(pagina(3, 100));
        when(listas.listarArquivadas(BUSCA, 3)).thenReturn(pagina(3, 100));
        when(veiculos.listar(BUSCA, 3)).thenReturn(pagina(3, 100));
        when(veiculos.listarArquivados(BUSCA, 3)).thenReturn(pagina(3, 100));
        when(especificacoes.listar(BUSCA, false, 3)).thenReturn(pagina(3, 100));
        when(especificacoes.listar(BUSCA, true, 3)).thenReturn(pagina(3, 100));
    }

    @ParameterizedTest
    @MethodSource("tabelas")
    void navegaPreservandoBuscaEContexto(String caminho, String parametro, String extra, String alvo) throws Exception {
        String query = parametro + "=__PAGINA__"
                + (caminho.equals("/especificacoes/listar") ? "&amp;arquivadas=" + extra : "");
        mvc.perform(get(caminho).param(parametro, BUSCA).param("pagina", "3")
                        .queryParam("arquivadas", extra).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hx-get=\"" + caminho + "?" + query + "&amp;pagina=0\"")))
                .andExpect(content().string(containsString("hx-get=\"" + caminho + "?" + query + "&amp;pagina=3\"")))
                .andExpect(content().string(containsString("hx-get=\"" + caminho + "?" + query + "&amp;pagina=99\"")))
                .andExpect(content().string(containsString("aria-current=\"page\"")))
                .andExpect(content().string(containsString("hx-target=\"" + alvo + "\"")))
                .andExpect(content().string(not(containsString("pagina=50"))));
    }

    static Stream<Arguments> tabelas() {
        String cidade = "/localidade/" + CIDADE + "/destinos";
        return Stream.of(
                Arguments.of("/localidade/listar", "busca", "false", "#cidadesAtivasContainer"),
                Arguments.of("/localidade/listarArquivado", "busca", "false", "#modal"),
                Arguments.of(cidade, "busca", "false", "#destinosContainer"),
                Arguments.of(cidade + "/arquivados", "busca", "false", "#destinosContainer"),
                Arguments.of("/listas/listar", "busca", "false", "#listasAtivasContainer"),
                Arguments.of("/listas/listarArquivadas", "busca", "false", "#modal"),
                Arguments.of("/veiculo/listar", "busca", "false", "#veiculosAtivosContainer"),
                Arguments.of("/veiculo/listarArquivado", "busca", "false", "#modal"),
                Arguments.of("/especificacoes/listar", "nome", "false", "#especificacoes-lista"),
                Arguments.of("/especificacoes/listar", "nome", "true", "#modal"));
    }

    @Test
    void primeiraEUltimaPaginaDesabilitamNavegacaoNosLimites() throws Exception {
        when(especificacoes.listar("Ana & João", false, 0)).thenReturn(pagina(0, 100));
        when(especificacoes.listar("Ana & João", false, 99)).thenReturn(pagina(99, 100));
        mvc.perform(get("/especificacoes/listar").param("nome", "Ana & João").param("pagina", "0")
                        .header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("nome=Ana%20%26%20Jo%C3%A3o")))
                .andExpect(content().string(containsString("pagina=99")))
                .andExpect(content().string(containsString("disabled=\"disabled\"")));
        mvc.perform(get("/especificacoes/listar").param("nome", "Ana & João").param("pagina", "99")
                        .header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("pagina=0")))
                .andExpect(content().string(containsString("disabled=\"disabled\"")));
        verify(especificacoes).listar("Ana & João", false, 99);
    }

    @Test
    void naoExibePaginacaoParaUmaPaginaOuNenhumResultado() throws Exception {
        when(especificacoes.listar("", false, 0)).thenReturn(pagina(0, 1));
        mvc.perform(get("/especificacoes/listar").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Páginas de especificações"))));
        when(especificacoes.listar("", false, 0)).thenReturn(pagina(0, 0));
        mvc.perform(get("/especificacoes/listar").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Páginas de especificações"))));
    }

    private static <T> ResultadoPaginado<T> pagina(int atual, int total) {
        return new ResultadoPaginado<>(List.of(), atual, 10, total * 10, total);
    }
}
