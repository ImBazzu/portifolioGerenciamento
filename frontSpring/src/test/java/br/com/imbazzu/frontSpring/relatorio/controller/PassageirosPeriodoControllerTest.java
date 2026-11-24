package br.com.imbazzu.frontSpring.relatorio.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClient;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;

class PassageirosPeriodoControllerTest {
    private MockMvc mvc;
    private MockRestServiceServer backend;
    private static final String PESSOA = "00000000-0000-0000-0000-000000000001";
    private static final String LISTA = "00000000-0000-0000-0000-000000000002";
    private static final String CIDADE = "00000000-0000-0000-0000-000000000003";
    private static final String DESTINO = "00000000-0000-0000-0000-000000000004";
    private static final String TIPO = "00000000-0000-0000-0000-000000000005";

    @BeforeEach
    void configurar() {
        var builder = RestClient.builder().baseUrl("http://localhost:8080/api");
        backend = MockRestServiceServer.bindTo(builder).build();
        RetryComRefreshPorta retry = new RetryComRefreshPorta() {
            public <T> T executar(Supplier<T> chamada) { return chamada.get(); }
        };
        mvc = MockMvcBuilders.standaloneSetup(
                new PassageirosPeriodoController(new RestClientProtegido(builder.build()), retry)).build();
    }

    private void respostaBackend() {
        String linha = """
                {"idPessoa":"%s","nome":"Ana","cpf":"123","telefone":"456","especificacao":"Especial",
                "data":"2030-01-%s","idLista":"%s","lista":"Saude","idCidade":"%s","cidade":"Cidade",
                "idDestino":"%s","destino":"Hospital","idTipoVeiculo":"%s","tipoVeiculo":"Carro",
                "horarioChegada":"10:00:00","tempoViagemSegundos":3600,"tempoViagemNanos":0,"acompanhantes":1,
                "horarioSaida":"07:30:00"}
                """;
        String json = "[" + linha.formatted(PESSOA, "01", LISTA, CIDADE, DESTINO, TIPO) + ","
                + linha.formatted(PESSOA, "02", LISTA, CIDADE, DESTINO, TIPO) + "]";
        backend.expect(requestTo("http://localhost:8080/api/relatorios/passageiros-periodo?dataInicio=2030-01-01&dataFim=2030-01-31"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    }

    private void respostaOpcoes() {
        String json = """
                {"listas":[{"id":"%s","nome":"Saude"}],"cidades":[{"id":"%s","nome":"Cidade"}],
                 "destinos":[{"id":"%s","nome":"Hospital","idCidade":"%s"}],
                 "tiposVeiculo":[{"id":"%s","nome":"Carro"}]}
                """.formatted(LISTA, CIDADE, DESTINO, CIDADE, TIPO);
        backend.expect(requestTo("http://localhost:8080/api/relatorios/passageiros-opcoes"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    }

    @Test
    void opcoesCarregamSemExigirPeriodo() throws Exception {
        respostaOpcoes();
        mvc.perform(get("/relatorios/passageiros/opcoes"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Hospital")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Saude")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Carro")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Ana"))));
        backend.verify();
    }

    @Test
    void pdfAgrupaPessoaEAplicaFiltros() throws Exception {
        respostaBackend();
        respostaOpcoes();
        var resposta = mvc.perform(get("/relatorios/passageiros/pdf").param("dataInicio", "2030-01-01")
                .param("dataFim", "2030-01-31").param("listas", LISTA, "00000000-0000-0000-0000-000000000099")
                .param("cidade", CIDADE).param("destino", DESTINO).param("tipoVeiculo", TIPO))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn().getResponse();
        var reader = new PdfReader(resposta.getContentAsByteArray());
        var extrator = new PdfTextExtractor(reader);
        String texto = extrator.getTextFromPage(1);
        assertTrue(texto.contains("PASSAGEIROS: 1"));
        assertTrue(texto.contains("AGENDAMENTOS: 2"));
        assertTrue(texto.contains("01/01/2030"));
        assertTrue(texto.contains("02/01/2030"));
        assertTrue(texto.contains("07:30"));
        assertFalse(texto.contains("09:00"));
        assertFalse(texto.contains("Saída necessária"));
        assertTrue(texto.contains("Saída programada"));
        assertTrue(texto.contains("123"));
        assertTrue(texto.contains("456"));
        assertTrue(texto.contains("Especial"));
        assertEquals(texto.indexOf("Ana"), texto.lastIndexOf("Ana"));
        assertTrue(reader.getPageN(1).getAsDict(PdfName.RESOURCES).getAsDict(PdfName.XOBJECT).size() > 0);
        reader.close();
        backend.verify();
    }

    @Test
    void filtroSemResultadoNaoIncluiPassageiros() throws Exception {
        for (int i = 0; i < 4; i++) {
            respostaBackend();
            respostaOpcoes();
        }
        for (String filtro : new String[] {"listas", "cidade", "destino", "tipoVeiculo"}) {
            var resposta = mvc.perform(get("/relatorios/passageiros/pdf").param("dataInicio", "2030-01-01")
                    .param("dataFim", "2030-01-31").param(filtro, "00000000-0000-0000-0000-000000000099"))
                    .andExpect(status().isOk()).andReturn().getResponse();
            var reader = new PdfReader(resposta.getContentAsByteArray());
            String texto = new PdfTextExtractor(reader).getTextFromPage(1);
            assertTrue(texto.contains("Nenhum agendamento encontrado"));
            assertFalse(texto.contains("Ana"));
            reader.close();
        }
        backend.verify();
    }

    @Test
    void camposVaziosIncluemTodos() throws Exception {
        respostaBackend();
        respostaOpcoes();
        mvc.perform(get("/relatorios/passageiros/pdf").param("dataInicio", "2030-01-01")
                .param("dataFim", "2030-01-31").param("cidade", "").param("destino", "")
                .param("tipoVeiculo", "")).andExpect(status().isOk());
        backend.verify();
    }

    @Test
    void datasInvertidasSaoRejeitadas() throws Exception {
        mvc.perform(get("/relatorios/passageiros/pdf").param("dataInicio", "2030-02-01")
                .param("dataFim", "2030-01-01")).andExpect(status().isBadRequest());
        backend.verify();
    }

    @Test
    void pdfRepeteIdentificacaoEmPaginaSeguinteESaidaNaoInformada() throws Exception {
        var inicio = java.time.LocalDate.of(2030, 1, 1);
        var viagens = java.util.stream.IntStream.range(0, 60).mapToObj(i -> new PassageiroPeriodo(
                java.util.UUID.fromString(PESSOA), "Ana", "123", "456", "Especial", inicio.plusDays(i),
                java.util.UUID.fromString(LISTA), "Saude", java.util.UUID.fromString(CIDADE), "Cidade",
                java.util.UUID.fromString(DESTINO), "Hospital", java.util.UUID.fromString(TIPO), "Carro",
                java.time.LocalTime.of(10, 0), 3600, 0, 1, null, null, null)).toList();
        var pdf = PassageirosPeriodoPdf.gerar(inicio, inicio.plusDays(59),
                java.util.List.of("Listas: Todos", "Cidade: Todas", "Destino: Todos", "Veículo: Todos"), viagens);
        var reader = new PdfReader(pdf);
        var extrator = new PdfTextExtractor(reader);
        assertTrue(reader.getNumberOfPages() > 1);
        for (int pagina = 1; pagina <= reader.getNumberOfPages(); pagina++) {
            assertTrue(extrator.getTextFromPage(pagina).contains("Ana"));
        }
        assertTrue(extrator.getTextFromPage(reader.getNumberOfPages()).contains("01/03/2030"));
        assertTrue(extrator.getTextFromPage(1).contains("Não informado"));
        reader.close();
    }
}
