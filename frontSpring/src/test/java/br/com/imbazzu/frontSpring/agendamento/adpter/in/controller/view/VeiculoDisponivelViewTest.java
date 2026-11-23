package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoPermitidoDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificacaoVeiculosResponseDto;

class VeiculoDisponivelViewTest {

    @Test
    void retornaSomenteVeiculosPermitidosComVagasSuficientesECarroSemAlocacaoFisica() {
        var idVan = UUID.randomUUID();
        var idCarro = UUID.randomUUID();
        var idMicroOnibus = UUID.randomUUID();
        var verificacao = new VerificacaoVeiculosResponseDto(List.of(
                new VeiculoPermitidoDto(idVan, "Van", 8, List.of()),
                new VeiculoPermitidoDto(idCarro, "Carro", 4, List.of())), List.of());
        var agenda = new AgendaDiaResponseDto(LocalDate.of(2026, 11, 23), List.of(
                veiculo(idVan, "Van", 8, "VAN-01", 3),
                veiculo(idVan, "Van", 8, "VAN-02", 6),
                veiculo(idMicroOnibus, "Micro-ônibus", 20, "MICRO-01", 0)), List.of());

        var disponiveis = VeiculoDisponivelView.disponiveis(verificacao, agenda, 2);

        assertEquals(2, disponiveis.size());
        assertEquals("VAN-01", disponiveis.get(0).identificacao());
        assertNull(disponiveis.get(1).idVeiculo());
    }

    private static VeiculoAbertoResponseDto veiculo(
            UUID idTipo, String nomeTipo, int capacidade, String identificacao, int ocupados) {
        return new VeiculoAbertoResponseDto(
                UUID.randomUUID(), LocalDate.of(2026, 11, 23), idTipo, nomeTipo, capacidade,
                identificacao, LocalTime.of(8, 0), ocupados, List.of());
    }
}
