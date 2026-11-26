package br.com.imbazzu.frontSpring.motorista;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MotoristaPaginaTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private MotoristaApi api;

    @Test
    void selecionaPorNomeECategoriaSemPerderFiltroNaPaginacao() throws Exception {
        var categoria = UUID.randomUUID();
        var veiculo = UUID.randomUUID();
        var motorista = UUID.randomUUID();
        when(api.categorias()).thenReturn(List.of(new MotoristaApi.Categoria(categoria, "Noturno")));
        when(api.buscar("Ana", categoria, 0)).thenReturn(new MotoristaApi.Pagina(
                List.of(new MotoristaApi.Motorista(motorista, "Ana", "11987654321", categoria, "Noturno")), 2, 0));
        mvc.perform(get("/motoristas/selecionar/{id}", veiculo).param("data", "2030-01-01")
                .param("carros", "true").param("nome", "Ana").param("categoria", categoria.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Noturno")))
                .andExpect(content().string(containsString("nome=Ana&amp;categoria=" + categoria + "&amp;pagina=1")))
                .andExpect(content().string(containsString(motorista.toString())));
        mvc.perform(post("/motoristas/selecionar/{id}", veiculo).param("data", "2030-01-01")
                .param("carros", "true").param("motoristaId", motorista.toString()))
                .andExpect(redirectedUrl("/agendamentos/carros?data=2030-01-01"));
        verify(api).atribuir(veiculo, motorista);
    }

    @Test
    void renderizaCadastroECategorias() throws Exception {
        var categoria = UUID.randomUUID();
        var motorista = UUID.randomUUID();
        when(api.categorias()).thenReturn(List.of(new MotoristaApi.Categoria(categoria, "Diurno")));
        when(api.buscar("", null, 0)).thenReturn(new MotoristaApi.Pagina(List.of(
                new MotoristaApi.Motorista(motorista, "Carlos", "11987654321", categoria, "Diurno")), 1, 0));
        var html = mvc.perform(get("/motoristas").flashAttr("novoNome", "Ana")
                        .flashAttr("novoTelefone", "123").flashAttr("novaCategoria", categoria)
                        .flashAttr("categoriaNova", "Noturno"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Carlos")))
                .andExpect(content().string(containsString("data-bs-target=\"#categoriasModal\"")))
                .andExpect(content().string(containsString("data-bs-target=\"#novoMotoristaModal\"")))
                .andExpect(content().string(containsString("data-bs-target=\"#editarMotorista-" + motorista + "\"")))
                .andExpect(content().string(not(containsString("<details"))))
                .andReturn().getResponse().getContentAsString();
        assertTrue(html.indexOf("id=\"categoriasModal\"") < html.indexOf("id=\"categoriaNova\""));
        assertTrue(html.indexOf("id=\"novoMotoristaModal\"") < html.indexOf("id=\"novoNome\""));
        assertTrue(html.indexOf("id=\"editarMotorista-" + motorista + "\"") < html.indexOf("id=\"nome-" + motorista + "\""));
        assertTrue(html.contains("value=\"Noturno\""));
        assertTrue(html.contains("value=\"Ana\""));
        assertTrue(html.contains("value=\"123\""));
        assertTrue(html.contains("value=\"" + categoria + "\" selected=\"selected\""));
    }

    @Test
    void mantemDadosDoNovoMotoristaQuandoApiRejeitaTelefone() throws Exception {
        var categoria = UUID.randomUUID();
        doThrow(HttpClientErrorException.create(HttpStatus.UNPROCESSABLE_ENTITY, "erro",
                org.springframework.http.HttpHeaders.EMPTY, "{\"mensagem\":\"Telefone inválido\"}".getBytes(), null))
                .when(api).salvar(null, "Ana", "123", categoria);
        mvc.perform(post("/motoristas").param("nome", "Ana").param("telefone", "123")
                .param("categoriaId", categoria.toString()))
                .andExpect(redirectedUrl("/motoristas"))
                .andExpect(flash().attribute("erro", "Telefone inválido"))
                .andExpect(flash().attribute("novoNome", "Ana"))
                .andExpect(flash().attribute("novoTelefone", "123"))
                .andExpect(flash().attribute("novaCategoria", categoria));
    }

    @Test
    void semCategoriaOrientaCadastroEImpedeCriarMotorista() throws Exception {
        when(api.categorias()).thenReturn(List.of());
        when(api.buscar("", null, 0)).thenReturn(new MotoristaApi.Pagina(List.of(), 0, 0));
        mvc.perform(get("/motoristas"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cadastre uma categoria antes de cadastrar motoristas.")))
                .andExpect(content().string(containsString("data-bs-target=\"#novoMotoristaModal\" disabled=\"disabled\"")))
                .andExpect(content().string(containsString("data-bs-target=\"#categoriasModal\"")));
    }
}
