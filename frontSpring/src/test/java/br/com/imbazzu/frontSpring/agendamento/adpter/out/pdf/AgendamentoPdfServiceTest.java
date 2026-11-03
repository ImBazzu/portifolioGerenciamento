package br.com.imbazzu.frontSpring.agendamento.adpter.out.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.AgendamentoController.PassageiroDetalheView;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;

class AgendamentoPdfServiceTest {

    @Test
    void geraPdfComDadosDoVeiculoEPassageiro() throws java.io.IOException {
        var data = LocalDate.of(2026, 11, 2);
        var idPessoa = UUID.randomUUID();
        var passageiro = new AgendamentoResponseDto(
                UUID.randomUUID(), data, idPessoa, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), LocalTime.of(8, 0), LocalTime.of(7, 0), true, 1, UUID.randomUUID(), List.of());
        var veiculo = new VeiculoAbertoResponseDto(
                UUID.randomUUID(), data, UUID.randomUUID(), "Carro", 4, "Carro 1",
                LocalTime.of(7, 0), 2, List.of(passageiro));
        var detalhes = Map.of(idPessoa,
                new PassageiroDetalheView("Robert", "11999999999", "Rua A, 10", "Aracaju", "Cirurgia"));

        var pdf = AgendamentoPdfService.gerar(data, "Relatório de carros", List.of(veiculo), List.of(), detalhes);

        assertEquals("%PDF", new String(pdf, 0, 4, StandardCharsets.US_ASCII));
        var leitor = new PdfReader(pdf);
        var texto = new PdfTextExtractor(leitor).getTextFromPage(1);
        assertTrue(texto.contains("Robert"));
        assertTrue(texto.contains("Rua A, 10"));
        leitor.close();
    }
}
