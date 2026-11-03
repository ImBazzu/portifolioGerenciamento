package br.com.imbazzu.frontSpring.agendamento.adpter.out.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.AgendamentoController.PassageiroDetalheView;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;

class AgendamentoPdfServiceTest {

    @Test
    void geraPdfUnicoDeCarrosIncluiPassageirosAlocadosEPendentesSemSaidaPorPassageiro()
            throws java.io.IOException {
        var data = LocalDate.of(2026, 11, 2);
        var idAlocado1 = UUID.randomUUID();
        var idAlocado2 = UUID.randomUUID();
        var idPendente = UUID.randomUUID();
        var passageiro1 = passageiro(data, idAlocado1, 1);
        var passageiro2 = passageiro(data, idAlocado2, 0);
        var passageiroPendente = passageiro(data, idPendente, 5);
        var veiculo1 = new VeiculoAbertoResponseDto(
                UUID.randomUUID(), data, UUID.randomUUID(), "Carro", 4, "Carro 1",
                LocalTime.of(7, 0), 2, List.of(passageiro1));
        var veiculo2 = new VeiculoAbertoResponseDto(
                UUID.randomUUID(), data, UUID.randomUUID(), "Carro", 4, "Carro 2",
                LocalTime.of(8, 0), 1, List.of(passageiro2));
        var detalhes = Map.of(
                idAlocado1, detalhe("Alocado Um"),
                idAlocado2, detalhe("Alocado Dois"),
                idPendente, detalhe("Pendente"));

        var pdf = AgendamentoPdfService.gerar(
                data, "Relatório geral de carros", List.of(veiculo1, veiculo2), List.of(passageiroPendente), detalhes);

        assertEquals("%PDF", new String(pdf, 0, 4, StandardCharsets.US_ASCII));
        var leitor = new PdfReader(pdf);
        var texto = new PdfTextExtractor(leitor).getTextFromPage(1);
        assertTrue(texto.contains("Alocado Um"));
        assertTrue(texto.contains("Alocado Dois"));
        assertTrue(texto.contains("Pendente"));
        assertTrue(texto.contains("Chegada"));
        assertTrue(texto.contains("Qtd. acompanhantes"));
        assertTrue(texto.contains("5"));
        assertFalse(texto.contains("Saída necessária"));
        leitor.close();
    }

    @Test
    void geraPdfIndividualComDuasViasLogoPlacaAssinaturasEPassageiros() throws java.io.IOException {
        var data = LocalDate.of(2026, 11, 2);
        var idPessoa = UUID.randomUUID();
        var passageiro = passageiro(data, idPessoa, 2);
        var veiculo = new VeiculoAbertoResponseDto(
                UUID.randomUUID(), data, UUID.randomUUID(), "Van", 15, "Van 1",
                LocalTime.of(7, 0), 3, List.of(passageiro));

        var pdf = AgendamentoPdfService.gerarIndividual(data, veiculo, Map.of(idPessoa, detalhe("Maria Silva")));

        var leitor = new PdfReader(pdf);
        assertEquals(2, leitor.getNumberOfPages());
        for (int pagina = 1; pagina <= 2; pagina++) {
            var texto = new PdfTextExtractor(leitor).getTextFromPage(pagina);
            assertTrue(texto.contains("Placa"));
            assertTrue(texto.contains("Assinatura do motorista"));
            assertTrue(texto.contains("Conferência da secretaria"));
            assertTrue(texto.contains("Maria Silva"));
            var recursos = leitor.getPageN(pagina).getAsDict(PdfName.RESOURCES);
            assertTrue(recursos.getAsDict(PdfName.XOBJECT).size() > 0);
        }
        assertTrue(new PdfTextExtractor(leitor).getTextFromPage(1).contains("VIA DO MOTORISTA"));
        assertTrue(new PdfTextExtractor(leitor).getTextFromPage(2).contains("VIA DA SECRETARIA"));
        leitor.close();
    }

    private static AgendamentoResponseDto passageiro(LocalDate data, UUID idPessoa, int acompanhantes) {
        return new AgendamentoResponseDto(
                UUID.randomUUID(), data, idPessoa, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), LocalTime.of(8, 0), LocalTime.of(7, 0), true, acompanhantes, UUID.randomUUID(),
                List.of());
    }

    private static PassageiroDetalheView detalhe(String nome) {
        return new PassageiroDetalheView(nome, "11999999999", "Rua A, 10", "Aracaju", "Cirurgia");
    }
}
