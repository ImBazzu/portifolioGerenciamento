package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.ui.Model;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.CarregadorOpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MontadorDetalhesPassageiros;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.OpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;
import br.com.imbazzu.frontSpring.motorista.MotoristaApi;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AgendamentoInsercaoWebTest {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private AgendamentoApi api;
    @MockitoBean
    private CarregadorOpcoesAgendamento carregador;
    @MockitoBean
    private MontadorDetalhesPassageiros detalhes;
    @MockitoBean
    private MotoristaApi motoristas;

    @Test
    void modalRenderizaEncaixeFixoEIncluiIdNoFormulario() throws Exception {
        var data = LocalDate.of(2026, 12, 1);
        var carro = new VeiculoAbertoResponseDto(UUID.randomUUID(), data, UUID.randomUUID(),
                "Carro", 4, null, null, 0, List.of());
        var encaixe = new ListaResponseDto(UUID.randomUUID(), "Encaixe", "", 0, 0, false,
                List.of(), List.of(), List.of(), List.of(), "ENCAIXE");
        var opcoes = new OpcoesAgendamento(List.of(encaixe), List.of(), List.of(), List.of());
        when(api.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(carro), List.of()));
        when(carregador.carregarParaUsuario()).thenReturn(opcoes);
        doAnswer(invocacao -> {
            Model model = invocacao.getArgument(0);
            model.addAttribute("listas", opcoes.listas());
            return null;
        }).when(carregador).adicionarAoModel(any(), any());

        mvc.perform(get("/agendamentos/carros/veiculos/{id}/modal-agendamento", carro.id())
                        .param("data", data.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesPattern("(?s).*<select[^>]*id=\"listaAgendamento\"[^>]*disabled=\"disabled\".*")))
                .andExpect(content().string(containsString("name=\"idLista\" value=\"" + encaixe.id() + "\"")))
                .andExpect(content().string(containsString("Encaixe")));
    }

    @Test
    void paginaMostraMenuDeOpcoesParaOnibusEInsercao() throws Exception {
        var data = LocalDate.of(2026, 12, 1);
        var onibus = new VeiculoAbertoResponseDto(UUID.randomUUID(), data, UUID.randomUUID(),
                "Ônibus", 40, "BUS-01", java.time.LocalTime.of(8, 0), 0, List.of());
        var opcoes = new OpcoesAgendamento(List.of(), List.of(), List.of(), List.of());
        when(api.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(onibus), List.of()));
        when(carregador.carregarParaUsuario()).thenReturn(opcoes);
        when(carregador.incluirDestinosHistoricos(any(), any())).thenReturn(opcoes);
        when(carregador.incluirDestinosHistoricosCancelados(any(), any())).thenReturn(opcoes);
        when(motoristas.atribuicoes(data)).thenReturn(List.of());
        doAnswer(invocacao -> {
            Model model = invocacao.getArgument(0);
            model.addAttribute("listas", List.of());
            model.addAttribute("tiposVeiculo", List.of());
            return null;
        }).when(carregador).adicionarAoModel(any(), any());

        mvc.perform(get("/agendamentos").param("data", data.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("aria-label=\"Opções do veículo\"")))
                .andExpect(content().string(containsString("bi-three-dots-vertical")))
                .andExpect(content().string(containsString("data-bs-target=\"#atribuirPassageiroModal-" + onibus.id() + "\"")));
    }
}
