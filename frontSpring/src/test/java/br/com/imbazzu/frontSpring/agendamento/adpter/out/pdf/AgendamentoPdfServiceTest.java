package br.com.imbazzu.frontSpring.agendamento.adpter.out.pdf;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfName;
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
                List.of(inicioNoturno, antesNoturno, indefinido), List.of(), Map.of(), Map.of()));
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
                List.of(onibus, madrugada, manha), List.of(), detalhes, Map.of(onibus.id(), "Motorista Ana")));
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
            assertTrue(primeira.contains("Motorista Ana"), primeira);
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
        var reader = new PdfReader(AgendamentoPdfService.gerarComprovante(agendamento, detalhe, veiculo, "Motorista Ana"));
        try {
            String texto = new PdfTextExtractor(reader).getTextFromPage(1);
            for (String esperado : List.of("Maria", "01/12/2026", "Salvador", "Hospital",
                    "Rua das Flores", "06:00", "Motorista Ana", agendamento.id().toString())) {
                assertTrue(texto.contains(esperado), texto);
            }
        } finally {
            reader.close();
        }
    }

    @Test
    void carroLotadoTemDuasViasCompletasEmUmaPagina() throws Exception {
        var passageiros = java.util.stream.IntStream.range(0, 4).mapToObj(i ->
                new AgendamentoResponseDto(UUID.randomUUID(), DATA, UUID.randomUUID(), UUID.randomUUID(),
                        UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalTime.of(8, i),
                        LocalTime.of(7, 0), true, 0, null, List.of())).toList();
        var detalhes = new java.util.HashMap<UUID, PassageiroDetalheView>();
        for (int i = 0; i < passageiros.size(); i++) {
            detalhes.put(passageiros.get(i).id(), new PassageiroDetalheView("Passageiro " + i,
                    "", "11987654321", "Rua das Flores número " + i + " Bairro Centro", "",
                    "Salvador", "Hospital Central", 0, "", true));
        }
        var carro = veiculo("Carro", "Carro 01", LocalTime.of(6, 0), 4, 4, passageiros);
        var reader = new PdfReader(AgendamentoPdfService.gerarIndividual(DATA, carro, detalhes, "Motorista Ana"));
        try {
            assertEquals(1, reader.getNumberOfPages());
            String texto = new PdfTextExtractor(reader).getTextFromPage(1);
            assertTrue(texto.contains("VIA DO MOTORISTA"), texto);
            assertTrue(texto.contains("VIA DA SECRETARIA"), texto);
            assertEquals(2, texto.split("Relação de passageiros", -1).length - 1, texto);
            assertTrue(reader.getPageN(1).getAsDict(PdfName.RESOURCES).getAsDict(PdfName.XOBJECT).size() > 0);
            String comandos = new String(reader.getPageContent(1), java.nio.charset.StandardCharsets.ISO_8859_1);
            assertEquals(2, java.util.regex.Pattern.compile("\\sDo\\s").matcher(comandos).results().count());
            for (String esperado : List.of("Motorista Ana", "06:00",
                    "Passageiro 0", "Passageiro 1", "Passageiro 2", "Passageiro 3")) {
                assertEquals(2, texto.split(java.util.regex.Pattern.quote(esperado), -1).length - 1, texto);
            }
            assertEquals(8, texto.split("Rua das Flores", -1).length - 1);
            assertEquals(8, texto.split("Hospital Central", -1).length - 1);
            assertTrue(texto.contains("Residência"), texto);
        } finally {
            reader.close();
        }
    }

    @Test
    void carroComEnderecosLongosPreservaTextoNasDuasVias() throws Exception {
        var passageiros = java.util.stream.IntStream.range(0, 4).mapToObj(i ->
                new AgendamentoResponseDto(UUID.randomUUID(), DATA, UUID.randomUUID(), UUID.randomUUID(),
                        UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalTime.of(8, i),
                        LocalTime.of(7, 0), true, 0, null, List.of())).toList();
        var detalhes = new java.util.HashMap<UUID, PassageiroDetalheView>();
        for (int i = 0; i < 4; i++) {
            detalhes.put(passageiros.get(i).id(), new PassageiroDetalheView("Pessoa " + i, "", "123",
                    "Rua de Santa Maria das Flores, próximo à igreja da comunidade, "
                            + "casa azul, bairro São José, referência final" + i, "",
                    "Cidade", "Hospital", 0, "", true));
        }
        var reader = new PdfReader(AgendamentoPdfService.gerarIndividual(DATA,
                veiculo("Carro", "01", LocalTime.of(6, 0), 4, 4, passageiros), detalhes, null));
        try {
            assertEquals(1, reader.getNumberOfPages());
            String texto = new PdfTextExtractor(reader).getTextFromPage(1);
            for (int i = 0; i < 4; i++) {
                assertEquals(2, texto.split("final" + i, -1).length - 1, texto);
            }
        } finally {
            reader.close();
        }
    }

    @Test
    void veiculoMaiorContinuaComViasSeparadasEMotoristaNaoAtribuido() throws Exception {
        var reader = new PdfReader(AgendamentoPdfService.gerarIndividual(DATA,
                veiculo("Van", "01", LocalTime.of(6, 0), 8, 0, List.of()), Map.of(), null));
        try {
            assertEquals(2, reader.getNumberOfPages());
            var extrator = new PdfTextExtractor(reader);
            assertTrue(extrator.getTextFromPage(1).contains("VIA DO MOTORISTA"));
            assertTrue(extrator.getTextFromPage(2).contains("VIA DA SECRETARIA"));
            assertTrue(extrator.getTextFromPage(1).contains("Não atribuído"));
        } finally {
            reader.close();
        }
    }

    @Test
    void naoOmitePassageiroSemDetalhesNemConteudoExtenso() {
        var passageiro = new AgendamentoResponseDto(UUID.randomUUID(), DATA, UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalTime.of(8, 0),
                LocalTime.of(7, 0), true, 0, null, List.of());
        var carro = veiculo("Carro", "01", LocalTime.of(6, 0), 4, 1, List.of(passageiro));
        assertThrows(IllegalStateException.class,
                () -> AgendamentoPdfService.gerarIndividual(DATA, carro, Map.of(), null));
        var detalhe = new PassageiroDetalheView("Maria", "", "123", "Endereço ".repeat(1000), "",
                "Cidade", "Hospital", 0, "", true);
        assertThrows(AgendamentoPdfService.ConteudoExtensoException.class,
                () -> AgendamentoPdfService.gerarIndividual(DATA, carro, Map.of(passageiro.id(), detalhe), null));
    }

    private static VeiculoAbertoResponseDto veiculo(String tipo, String nome, LocalTime saida,
            int capacidade, int ocupados, List<AgendamentoResponseDto> passageiros) {
        return new VeiculoAbertoResponseDto(UUID.randomUUID(), DATA, UUID.randomUUID(), tipo,
                capacidade, nome, saida, ocupados, passageiros);
    }
}
