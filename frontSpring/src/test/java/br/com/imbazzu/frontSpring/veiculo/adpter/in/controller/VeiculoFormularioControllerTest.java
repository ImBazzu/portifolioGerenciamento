package br.com.imbazzu.frontSpring.veiculo.adpter.in.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoHistoricoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;

class VeiculoFormularioControllerTest {

    @Test
    void carregaDetalhesEHistoricoDoTipoAtivo() {
        var application = mock(VeiculoApplicationPorta.class);
        var controller = new VeiculoFormularioController(application);
        var id = UUID.randomUUID();
        var veiculo = new TipoVeiculoResponseDto(id, "Van", 15);
        var historico = List.of(new TipoVeiculoHistoricoResponseDto(
                UUID.randomUUID(), LocalDate.of(2026, 11, 3), 1, "Aracaju", 12));
        var model = new ExtendedModelMap();
        when(application.buscar(id)).thenReturn(veiculo);
        when(application.buscarHistorico(id)).thenReturn(historico);

        var view = controller.detalhesVeiculo(id, false, model);

        assertEquals("veiculo/detalhesVeiculo :: detalhesVeiculo", view);
        assertEquals(false, model.getAttribute("arquivado"));
        assertSame(veiculo, model.getAttribute("veiculo"));
        assertSame(historico, model.getAttribute("historico"));
        verify(application).buscar(id);
        verify(application).buscarHistorico(id);
    }
}
