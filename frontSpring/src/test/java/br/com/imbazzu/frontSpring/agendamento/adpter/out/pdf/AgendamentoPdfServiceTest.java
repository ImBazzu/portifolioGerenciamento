package br.com.imbazzu.frontSpring.agendamento.adpter.out.pdf;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.PassageiroDetalheView;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;

class AgendamentoPdfServiceTest {
    private static final LocalDate DATA = LocalDate.of(2026, 12, 1);

    @Test
    void limiteNoturnoEHorarioIndefinidoSeguemFaixaDoFront() throws Exception {
        var inicioNoturno = veiculo("Ônibus", "Noturno", LocalTime.of(20, 0), 40, 0, List.of());
        var antesNoturno = veiculo("Ônibus", "Diurno", LocalTime.of(19, 59), 40, 0, List.of());
        var indefinido = veiculo("Carro", "Sem horário", null, 4, 0, List.of());
        var reader = new PdfReader(AgendamentoPdfService.gerar(DATA, "Carros",
                List.of(inicioNoturno, antesNoturno, indefinido), List.of(), Map.of()));
        try {
            var extrator = new PdfTextExtractor(reader);
            String noite = extrator.getTextFromPage(1);
            String dia = extrator.getTextFromPage(2);
            assertTrue(noite.contains("ANTES DAS 05:30"), noite);
            assertTrue(noite.contains("20:00"), noite);
            assertTrue(noite.contains("0 pessoas (0 passageiros e 0 acompanhantes)"), noite);
            assertTrue(dia.contains("A PARTIR DAS 05:30"), dia);
            assertTrue(dia.contains("19:59"), dia);
            assertTrue(dia.contains("Sem horário"), dia);
        } finally {
            reader.close();
        }
    }

    @Test
    void relatorioAgrupaNoturnoIncluiOnibusEEnderecoDeBusca() throws Exception {
        var pessoa = UUID.randomUUID();
        var passageiro = new AgendamentoResponseDto(UUID.randomUUID(), DATA, pessoa, UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalTime.of(8, 0),
                LocalTime.of(7, 0), true, 1, null, List.of());
        var semBusca = new AgendamentoResponseDto(UUID.randomUUID(), DATA, UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalTime.of(8, 0),
                LocalTime.of(7, 0), false, 1, null, List.of());
        var detalhes = Map.of(
                passageiro.id(), new PassageiroDetalheView("Maria", "cpf", "123",
                        "Rua das Flores", "", "Salvador", "Hospital", 1, "", true),
                semBusca.id(), new PassageiroDetalheView("João", "cpf", "123",
                        "Rua Central", "", "Alagoinhas", "Hospital", 1, "", true));
        var onibus = veiculo("Ônibus", "Salvador", LocalTime.of(23, 0), 40, 4, List.of(passageiro, semBusca));
        var madrugada = veiculo("Carro", "Madrugada", LocalTime.of(5, 29), 4, 0, List.of());
        var manha = veiculo("Carro", "Manhã", LocalTime.of(5, 30), 4, 0, List.of());

        var reader = new PdfReader(AgendamentoPdfService.gerar(DATA, "Carros",
                List.of(onibus, madrugada, manha), List.of(), detalhes));
        try {
            var extrator = new PdfTextExtractor(reader);
            String primeira = extrator.getTextFromPage(1);
            String busca = extrator.getTextFromPage(2);
            String depois = extrator.getTextFromPage(3);
            assertTrue(primeira.contains("ANTES DAS 05:30"), primeira);
            assertTrue(primeira.contains("SALVADOR"), primeira);
            assertTrue(primeira.contains("23:00"), primeira);
            assertTrue(primeira.contains("05:29"), primeira);
            assertTrue(primeira.contains("40"), primeira);
            assertTrue(primeira.contains("36"), primeira);
            assertTrue(primeira.contains("BUSCA NA RESIDÊNCIA"), primeira);
            assertTrue(primeira.contains("2 pessoas (1 passageiro e 1 acompanhante)"), primeira);
            assertTrue(!primeira.contains("Passageiros: 1"), primeira);
            assertTrue(primeira.contains("Salvador"), primeira);
            assertTrue(primeira.contains("Alagoinhas"), primeira);
            assertTrue(busca.contains("Maria") || busca.contains("MARIA"), busca);
            assertTrue(busca.contains("Rua das Flores"), busca);
            assertTrue(busca.contains("23:00"), busca);
            assertTrue(busca.contains("Ônibus"), busca);
            assertTrue(busca.contains("Ônibus 1"), busca);
            assertTrue(!busca.contains("Rua Central"), busca);
            assertTrue(depois.contains("A PARTIR DAS 05:30"), depois);
            assertTrue(depois.contains("05:30"), depois);
        } finally {
            reader.close();
        }
    }

    @Test
    void comprovanteMantemDadosDaViagem() throws Exception {
        var pessoa = UUID.randomUUID();
        var agendamento = new AgendamentoResponseDto(UUID.randomUUID(), DATA, pessoa, UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalTime.of(8, 0),
                LocalTime.of(7, 0), true, 1, null, List.of());
        var detalhe = new PassageiroDetalheView("Maria", "", "", "Rua das Flores",
                "", "Salvador", "Hospital", 1, "", true);
        var veiculo = veiculo("Carro", "01", LocalTime.of(6, 0), 4, 2, List.of(agendamento));
        var reader = new PdfReader(AgendamentoPdfService.gerarComprovante(agendamento, detalhe, veiculo));
        try {
            String texto = new PdfTextExtractor(reader).getTextFromPage(1);
            for (String esperado : List.of("Maria", "01/12/2026", "Salvador", "Hospital",
                    "Rua das Flores", "06:00", agendamento.id().toString())) {
                assertTrue(texto.contains(esperado), texto);
            }
        } finally {
            reader.close();
        }
    }

    private static VeiculoAbertoResponseDto veiculo(String tipo, String nome, LocalTime saida,
            int capacidade, int ocupados, List<AgendamentoResponseDto> passageiros) {
        return new VeiculoAbertoResponseDto(UUID.randomUUID(), DATA, UUID.randomUUID(), tipo,
                capacidade, nome, saida, ocupados, passageiros);
    }
}
