package br.com.imbazzu.frontSpring.relatorio.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class InvestigacaoPaginaTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private InvestigacaoApi api;

    @Test
    void investigaPessoaArquivadaComFiltrosEPaginacao() throws Exception {
        var id = UUID.randomUUID();
        var pessoa = new InvestigacaoApi.Pessoa(id, "Ana", "123", "11987654321", "Padrão", LocalDate.of(2030, 1, 1));
        var filtro = new InvestigacaoApi.Filtros(LocalDate.of(2030, 1, 1), LocalDate.of(2030, 1, 31), "São Paulo", "Hospital");
        var agendamento = new InvestigacaoApi.Agendamento(UUID.randomUUID(), LocalDate.of(2030, 1, 15),
                LocalTime.of(9, 0), "São Paulo", "Hospital", "Consulta", "Van", null, null, null, 2, true, null);
        when(api.pessoas("Ana", 0)).thenReturn(new InvestigacaoApi.Pagina<>(List.of(pessoa), 21, 2, 0));
        when(api.pessoa(id, filtro, "PENDENTE", 0)).thenReturn(new InvestigacaoApi.PessoaDetalhe(pessoa,
                new InvestigacaoApi.Pagina<>(List.of(agendamento), 41, 3, 0)));
        mvc.perform(get("/relatorios/investigacao/pessoas").param("nome", "Ana").param("pessoa", id.toString())
                        .param("inicio", "2030-01-01").param("fim", "2030-01-31")
                        .param("cidade", "São Paulo").param("destino", "Hospital").param("situacao", "PENDENTE"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Histórico: Ana")))
                .andExpect(content().string(containsString("Arquivada")))
                .andExpect(content().string(containsString("41 agendamento(s) no filtro")))
                .andExpect(content().string(containsString("Sem veículo atribuído")))
                .andExpect(content().string(containsString("Autor desconhecido")))
                .andExpect(content().string(containsString("cidade=S%C3%A3o%20Paulo")))
                .andExpect(content().string(containsString("situacao=PENDENTE&amp;pagina=2")));
        verify(api).pessoa(id, filtro, "PENDENTE", 0);
    }

    @Test
    void mostraMotoristasVeiculosEPassageirosComFiltrosPreservados() throws Exception {
        var id = UUID.randomUUID();
        var filtro = new InvestigacaoApi.Filtros(LocalDate.of(2030, 1, 1), null, "Cidade", "");
        var motorista = new InvestigacaoApi.Motorista(id, "Ana", "Noturno", 21, 2, 1);
        var passageiro = new InvestigacaoApi.Passageiro("Beatriz", "Cidade", "Hospital", LocalTime.of(9, 0), 1);
        var veiculo = new InvestigacaoApi.Veiculo(UUID.randomUUID(), LocalDate.of(2030, 1, 3), "Ana", "Noturno",
                "Van", null, LocalTime.of(7, 0), true, List.of(passageiro));
        var resultado = new InvestigacaoApi.Motoristas(new InvestigacaoApi.Resumo(1, 21, 2, 1),
                new InvestigacaoApi.Pagina<>(List.of(motorista), 1, 1, 0),
                new InvestigacaoApi.Pagina<>(List.of(veiculo), 21, 2, 0), null);
        when(api.motoristas("Ana", null, "Noturno", filtro, 0, 0, 0)).thenReturn(resultado);
        mvc.perform(get("/relatorios/investigacao/motoristas").param("nome", "Ana").param("categoria", "Noturno")
                        .param("inicio", "2030-01-01").param("cidade", "Cidade"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<strong>21</strong> veículos atribuídos")))
                .andExpect(content().string(containsString("Beatriz")))
                .andExpect(content().string(containsString("07:00")))
                .andExpect(content().string(containsString("(calculada)")))
                .andExpect(content().string(containsString("motorista=" + id)))
                .andExpect(content().string(containsString("categoria=Noturno")))
                .andExpect(content().string(containsString("pagina=1")));
    }

    @Test
    void mostraContagemPorCidadeDoMotoristaSelecionado() throws Exception {
        var id = UUID.randomUUID();
        var filtro = new InvestigacaoApi.Filtros(null, null, "", "");
        var cidades = new InvestigacaoApi.Pagina<>(List.of(
                new InvestigacaoApi.Cidade(UUID.randomUUID(), "Aracaju", "SE", 1),
                new InvestigacaoApi.Cidade(UUID.randomUUID(), "Itabaiana", "SE", 1)), 2, 1, 0);
        var resultado = new InvestigacaoApi.Motoristas(new InvestigacaoApi.Resumo(1, 1, 3, 2),
                new InvestigacaoApi.Pagina<>(List.of(), 0, 0, 0),
                new InvestigacaoApi.Pagina<>(List.of(), 0, 0, 0), cidades);
        when(api.motoristas("", id, "", filtro, 0, 0, 0)).thenReturn(resultado);
        mvc.perform(get("/relatorios/investigacao/motoristas").param("motorista", id.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<strong>2</strong> cidades distintas")))
                .andExpect(content().string(containsString("Aracaju / SE")))
                .andExpect(content().string(containsString("Itabaiana / SE")));
    }

    @Test
    void dataInvertidaMostraErroSemConsultarApi() throws Exception {
        mvc.perform(get("/relatorios/investigacao/motoristas")
                        .param("inicio", "2030-02-01").param("fim", "2030-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("A data inicial não pode ser posterior à data final.")));
        verifyNoInteractions(api);
    }
}
