package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.CarregadorOpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MensagemErroApi;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MontadorAgendaDia;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.RespostaFlash;
import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.FinalidadeAberturaVeiculo;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;

class AgendamentoPassageiroListaEspecialTest {
    @Test
    void naoVerificaNemCriaComListaDiferenteDoCarroEspecial() {
        var api = mock(AgendamentoApi.class);
        var data = LocalDate.of(2026, 12, 1);
        var veiculo = new VeiculoAbertoResponseDto(UUID.randomUUID(), data, UUID.randomUUID(),
                "Carro", 4, null, LocalTime.of(8, 0), 0, List.of(), null,
                FinalidadeAberturaVeiculo.EMERGENCIA, UUID.randomUUID());
        when(api.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(veiculo), List.of()));
        var controller = new AgendamentoPassageiroController(api, mock(PessoaApplicationPorta.class),
                mock(VeiculoApplicationPorta.class), mock(CarregadorOpcoesAgendamento.class),
                mock(MontadorAgendaDia.class), mock(RespostaFlash.class), mock(MensagemErroApi.class));
        var pessoa = UUID.randomUUID();
        var listaIncorreta = UUID.randomUUID();
        var destino = UUID.randomUUID();
        var hora = LocalTime.of(8, 0);

        assertThrows(ResponseStatusException.class, () -> controller.verificarVeiculos(
                data, pessoa, listaIncorreta, destino, hora, 0, false, true, veiculo.id(),
                true, null, new ExtendedModelMap(), new RedirectAttributesModelMap()));
        assertThrows(ResponseStatusException.class, () -> controller.criar(
                data, pessoa, listaIncorreta, UUID.randomUUID(), destino, hora, false, true,
                0, null, veiculo.id(), true, new RedirectAttributesModelMap()));
        verify(api, never()).verificarVeiculos(any(), any());
        verify(api, never()).criar(any(), any());
    }
}
