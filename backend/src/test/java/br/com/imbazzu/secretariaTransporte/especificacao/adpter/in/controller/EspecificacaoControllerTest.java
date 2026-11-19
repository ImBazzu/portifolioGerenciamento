package br.com.imbazzu.secretariaTransporte.especificacao.adpter.in.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.jpaAdapter.EspecificacaoQueryAdapter;
import br.com.imbazzu.secretariaTransporte.especificacao.application.query.buscar.EspecificacaoQueryBuscarHandler;
import br.com.imbazzu.secretariaTransporte.especificacao.core.domain.Especificacao;
import br.com.imbazzu.secretariaTransporte.prioridade.PrioridadeJpa;
import br.com.imbazzu.secretariaTransporte.prioridade.PrioridadeRepository;

class EspecificacaoControllerTest {
    @Test
    void rotaRetornaDetalhesDaEspecificacaoArquivada() throws Exception {
        var adapter = mock(EspecificacaoQueryAdapter.class);
        var prioridades = mock(PrioridadeRepository.class);
        var prioridade = new PrioridadeJpa();
        prioridade.setId(UUID.randomUUID());
        prioridade.setNome("Alta");
        var id = UUID.randomUUID();
        var especificacao = Especificacao.recriar(id, "Transporte", prioridade.getId(), "Observação", LocalDate.of(2026, 1, 15));
        when(adapter.buscarArquivado(id)).thenReturn(Optional.of(especificacao));
        when(prioridades.findById(prioridade.getId())).thenReturn(Optional.of(prioridade));
        var controller = new EspecificacaoController(null, null, null, null, null,
                new EspecificacaoQueryBuscarHandler(adapter), null, null, prioridades);

        MockMvcBuilders.standaloneSetup(controller).build()
                .perform(get("/especificacao/{id}/arquivado", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Transporte"))
                .andExpect(jsonPath("$.prioridadeNome").value("Alta"))
                .andExpect(jsonPath("$.dataArquivado").value("2026-01-15"));
    }
}
