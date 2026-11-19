package br.com.imbazzu.frontSpring.relatorio.controller;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClient;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;

class RelatorioDadosControllerTest {
    private MockMvc mvc;
    private MockRestServiceServer backend;

    @BeforeEach
    void configurar() {
        var builder = RestClient.builder().baseUrl("http://localhost:8080/api");
        backend = MockRestServiceServer.bindTo(builder).build();
        RetryComRefreshPorta retry = new RetryComRefreshPorta() {
            public <T> T executar(Supplier<T> chamada) {
                return chamada.get();
            }
        };
        mvc = MockMvcBuilders.standaloneSetup(
                new RelatorioDadosController(new RestClientProtegido(builder.build()), retry)).build();
    }

    @Test
    void encaminhaConsultaComDatasParaApi() throws Exception {
        backend.expect(requestTo("http://localhost:8080/api/relatorios/agendamentos?dataInicio=2026-01-01&dataFim=2026-01-31"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"totalAgendamentos\":3}", MediaType.APPLICATION_JSON));

        mvc.perform(get("/api/relatorios/agendamentos")
                .param("dataInicio", "2026-01-01").param("dataFim", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"totalAgendamentos\":3}"));
        backend.verify();
    }

    @Test
    void rejeitaDatasInvertidasETiposDesconhecidos() throws Exception {
        mvc.perform(get("/api/relatorios/pessoas")
                .param("dataInicio", "2026-02-01").param("dataFim", "2026-01-01"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/relatorios/outro")).andExpect(status().isNotFound());
        backend.verify();
    }
}
