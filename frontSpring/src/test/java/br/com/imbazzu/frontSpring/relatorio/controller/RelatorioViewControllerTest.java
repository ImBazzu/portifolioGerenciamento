package br.com.imbazzu.frontSpring.relatorio.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RelatorioViewControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void paginaRenderizaLayoutEFragmentosDeRelatorio() throws Exception {
        mvc.perform(get("/relatorios"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"sidebar-menu\"")))
                .andExpect(content().string(containsString("class=\"nav-link text-white active\"")))
                .andExpect(content().string(containsString("id=\"btnToggleSidebar\"")))
                .andExpect(content().string(containsString("id=\"filtroForm\"")))
                .andExpect(content().string(containsString("id=\"chartAgendamentosPeriodo\"")))
                .andExpect(content().string(containsString("id=\"tabelaPessoasFrequentes\"")))
                .andExpect(content().string(containsString("id=\"metricsVeiculos\"")))
                .andExpect(content().string(containsString("id=\"metricsOperacional\"")))
                .andExpect(content().string(containsString("/css/relatorios.css")))
                .andExpect(content().string(containsString("/js/relatorios.js")))
                .andExpect(content().string(not(containsString("fragmentos/cabecalho"))));
    }
}
