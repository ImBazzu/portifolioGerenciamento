package br.com.imbazzu.frontSpring.localidade.adpter.in.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.imbazzu.frontSpring.localidade.adpter.in.controller.LocalidadeFormularioController.CidadeFormulario;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeRequestDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;

class LocalidadeFormularioControllerTest {

    @Test
    void converteTempoDoFormularioParaRequisicao() {
        var application = mock(LocalidadeApplicationPorta.class);
        var controller = new LocalidadeFormularioController(application);
        var cidade = new CidadeFormulario("Cidade", "BA", LocalTime.of(23, 59));

        controller.salvar(cidade, new BeanPropertyBindingResult(cidade, "cidade"), new ExtendedModelMap());

        verify(application).salvarCidade(new CidadeRequestDto("Cidade", "BA", 23, 59));
    }

    @Test
    void recebeTempoComoCampoUnicoDoFormulario() throws Exception {
        var application = mock(LocalidadeApplicationPorta.class);
        var mvc = MockMvcBuilders.standaloneSetup(new LocalidadeFormularioController(application)).build();

        mvc.perform(post("/localidade").header("HX-Request", "true")
                .param("nome", "Cidade").param("estado", "BA").param("tempoViagem", "01:05"))
                .andExpect(status().isOk());

        verify(application).salvarCidade(new CidadeRequestDto("Cidade", "BA", 1, 5));
    }

    @Test
    void avisaSobreDuracaoAntigaQueNaoCabeNoCampo() {
        var application = mock(LocalidadeApplicationPorta.class);
        var id = UUID.randomUUID();
        when(application.buscarCidade(id)).thenReturn(new CidadeResponseDto(id, "Cidade", "BA", "25:15", 0));
        var model = new ExtendedModelMap();

        new LocalidadeFormularioController(application).formEditar(id, false, model);

        assertNull(((CidadeFormulario) model.get("cidade")).tempoViagem());
        assertNotNull(model.get("duracaoAntiga"));
    }

    @Test
    void carregaDuracaoExistenteSemZerosAEsquerda() {
        var application = mock(LocalidadeApplicationPorta.class);
        var id = UUID.randomUUID();
        when(application.buscarCidade(id)).thenReturn(new CidadeResponseDto(id, "Cidade", "BA", "1:5", 0));
        var model = new ExtendedModelMap();

        new LocalidadeFormularioController(application).formEditar(id, false, model);

        assertEquals(LocalTime.of(1, 5), ((CidadeFormulario) model.get("cidade")).tempoViagem());
    }
}
