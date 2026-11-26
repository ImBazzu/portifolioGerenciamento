package br.com.imbazzu.frontSpring.pessoa.adpter.in.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.agendamento.core.dto.HistoricoViagemResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AbrirVeiculoRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoPermitidoDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificacaoVeiculosResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificarVeiculosRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoAtualizarRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.porta.in.HistoricoViagemApplicationPorta;
import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.agendamento.adpter.out.pdf.AgendamentoPdfService;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.PassageiroDetalheView;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.PassageiroRelatorioView;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoResponseDto;
import br.com.imbazzu.frontSpring.especificacao.core.porta.in.EspecificacaoApplicationPorta;
import static org.hamcrest.Matchers.not;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaRequestDto;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.DestinoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontSpring.motorista.MotoristaApi;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PessoaFormularioWebTest {
    private static final UUID ESPECIFICACAO_ID = UUID.fromString("c5d0a150-2d80-4f0b-9a1d-3cfb8d8a0f22");
    private static final UUID PESSOA_ID = UUID.fromString("b4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f21");
    private static final UUID PRIORIDADE_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PessoaApplicationPorta pessoas;

    @MockitoBean
    private EspecificacaoApplicationPorta especificacoes;

    @MockitoBean
    private HistoricoViagemApplicationPorta historico;

    @MockitoBean
    private AgendamentoApi agendamentos;

    @MockitoBean
    private MotoristaApi motoristas;

    @MockitoBean
    private ListaApplicationPorta listas;

    @MockitoBean
    private LocalidadeApplicationPorta localidades;

    @MockitoBean
    private VeiculoApplicationPorta tiposVeiculo;

    @Test
    void pdfPassageirosPaginaSemPerderPassageiros() throws Exception {
        var data = LocalDate.of(2026, 11, 23);
        var agendamento = new AgendamentoResponseDto(UUID.randomUUID(), data, PESSOA_ID, UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalTime.of(9, 0), null,
                false, 0, null, List.of());
        var linhas = java.util.stream.IntStream.range(0, 40).mapToObj(numero -> new PassageiroRelatorioView(
                agendamento, new PassageiroDetalheView("Pessoa " + numero, "123", "456", "", "",
                        "Cidade", "Destino", 0, "Padrão"), "Lista", null)).toList();
        var reader = new PdfReader(AgendamentoPdfService.gerarPassageiros(data, linhas));
        assertTrue(reader.getNumberOfPages() > 1);
        var texto = new StringBuilder();
        var extractor = new PdfTextExtractor(reader);
        for (int pagina = 1; pagina <= reader.getNumberOfPages(); pagina++) {
            texto.append(extractor.getTextFromPage(pagina)).append('\n');
        }
        reader.close();
        for (int numero = 0; numero < 40; numero++) {
            assertTrue(texto.toString().contains("Pessoa " + numero + "\n"), "Ausente: Pessoa " + numero);
        }
    }

    @Test
    void pdfPassageirosRespeitaSelecaoOrdemEDadosDaAgenda() throws Exception {
        var data = LocalDate.of(2026, 11, 23);
        var listaId = UUID.randomUUID();
        var tipoId = UUID.randomUUID();
        var cidadeId = UUID.randomUUID();
        var destinoId = UUID.randomUUID();
        var veiculoId = UUID.randomUUID();
        var pessoaPendenteId = UUID.randomUUID();
        var idExcluido = UUID.randomUUID();
        var atribuido = new AgendamentoResponseDto(UUID.randomUUID(), data, PESSOA_ID, listaId,
                UUID.randomUUID(), tipoId, destinoId, LocalTime.of(9, 0), LocalTime.of(8, 0),
                true, 1, veiculoId, List.of());
        var pendente = new AgendamentoResponseDto(UUID.randomUUID(), data, pessoaPendenteId, listaId,
                UUID.randomUUID(), tipoId, destinoId, LocalTime.of(10, 0), null, false, 0, null, List.of());
        var excluido = new AgendamentoResponseDto(UUID.randomUUID(), data, idExcluido, listaId,
                UUID.randomUUID(), tipoId, destinoId, LocalTime.of(11, 0), null, false, 0, null, List.of());
        var veiculo = new VeiculoAbertoResponseDto(veiculoId, data, tipoId, "Van", 8,
                "VAN-01", LocalTime.of(7, 30), 2, List.of(atribuido));
        when(agendamentos.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(veiculo), List.of(pendente, excluido)));
        when(pessoas.buscar(PESSOA_ID)).thenReturn(new PessoaResponseDto(PESSOA_ID, "Ana Souza", "",
                ESPECIFICACAO_ID, "Gestante", "Alta", PRIORIDADE_ID, 2, true, 2, false,
                null, "Rua Principal 12", "52998224725", "11987654321"));
        when(pessoas.buscar(pessoaPendenteId)).thenReturn(new PessoaResponseDto(pessoaPendenteId, "Bruno Silva", "",
                ESPECIFICACAO_ID, "Padrão", "Alta", PRIORIDADE_ID, 2, false, 0, false,
                null, "Rua que nao deve aparecer", "12345678900", "11912345678"));
        when(pessoas.buscar(idExcluido)).thenReturn(new PessoaResponseDto(idExcluido, "Carlos Excluido", "",
                ESPECIFICACAO_ID, "Padrão", "Alta", PRIORIDADE_ID, 2, false, 0, false,
                null, "", "123", "456"));
        when(listas.listarAtivas("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
        when(listas.buscarArquivada(listaId)).thenReturn(new ListaResponseDto(
                listaId, "Lista histórica", "", 0, 0, true, List.of(), List.of(), List.of(), List.of()));
        when(tiposVeiculo.listar("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new TipoVeiculoResponseDto(tipoId, "Van", 8)), 0, 10, 1, 1));
        when(localidades.listarCidades("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new CidadeResponseDto(cidadeId, "Salvador", "BA", "", 1)), 0, 10, 1, 1));
        when(localidades.listarDestinos(cidadeId, "", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
        when(localidades.listarDestinosArquivados(cidadeId, "", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new DestinoResponseDto(destinoId, "Hospital Central", 1)), 0, 10, 1, 1));

        mvc.perform(get("/agendamentos").param("data", data.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-tipo-nome=\"Van\"")))
                .andExpect(content().string(containsString("data-cidade=\"Salvador\"")))
                .andExpect(content().string(containsString("data-especificacao=\"Gestante\"")))
                .andExpect(content().string(containsString("action=\"/agendamentos/pdf/passageiros\"")));

        var geracao = mvc.perform(post("/agendamentos/pdf/passageiros").param("data", data.toString())
                        .param("ids", pendente.id().toString(), atribuido.id().toString()))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(header().string("Location", matchesPattern(
                        ".*/agendamentos/pdf/passageiros/[0-9a-f-]+/passageiros-2026-11-23\\.pdf")))
                .andReturn();
        String endereco = geracao.getResponse().getHeader("Location");
        var sessao = (MockHttpSession) geracao.getRequest().getSession(false);
        byte[] pdf = mvc.perform(get(endereco).session(sessao))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition",
                        "inline; filename=\"passageiros-2026-11-23.pdf\""))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andReturn().getResponse().getContentAsByteArray();
        byte[] download = mvc.perform(get(endereco).session(sessao))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();
        assertArrayEquals(pdf, download);
        mvc.perform(get(endereco)).andExpect(status().isNotFound());
        mvc.perform(get(endereco).session(new MockHttpSession())).andExpect(status().isNotFound());
        mvc.perform(get(endereco.replace("passageiros-2026-11-23.pdf", "outro.pdf")).session(sessao))
                .andExpect(status().isNotFound());
        var reader = new PdfReader(pdf);
        var texto = new StringBuilder();
        var extractor = new PdfTextExtractor(reader);
        for (int pagina = 1; pagina <= reader.getNumberOfPages(); pagina++) {
            texto.append(extractor.getTextFromPage(pagina));
        }
        reader.close();
        String conteudo = texto.toString();
        assertTrue(conteudo.indexOf("Bruno Silva") < conteudo.indexOf("Ana Souza"), conteudo);
        for (String esperado : List.of("Bruno Silva", "Ana Souza", "52998224725", "11987654321",
                "DADOS DO PASSAGEIRO", "DADOS DA VIAGEM", "CPF: 12345678900", "Gestante",
                "Acompanhantes: 1", "Rua Principal 12", "Lista histórica", "Salvador",
                "Hospital Central", "Horário de chegada: 10:00", "VEÍCULO ATRIBUÍDO",
                "VEÍCULO PENDENTE", "09:00", "Van", "Saída: 07:30", "Pendente",
                "TOTAL DE PASSAGEIROS", "Passageiros cadastrados")) {
            assertTrue(conteudo.contains(esperado), esperado + " ausente em: " + conteudo);
        }
        assertFalse(conteudo.contains("Carlos Excluido"));
        assertFalse(conteudo.contains("Rua que nao deve aparecer"));

        mvc.perform(post("/agendamentos/pdf/passageiros").param("data", data.toString())
                        .param("ids", atribuido.id().toString(), atribuido.id().toString()))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/agendamentos/pdf/passageiros").param("data", data.toString())
                        .param("ids", atribuido.id().toString(), UUID.randomUUID().toString()))
                .andExpect(status().isConflict());

        String ultimoEndereco = null;
        for (int i = 0; i < 10; i++) {
            ultimoEndereco = mvc.perform(post("/agendamentos/pdf/passageiros").session(sessao)
                            .param("data", data.toString()).param("ids", atribuido.id().toString()))
                    .andExpect(status().isSeeOther())
                    .andReturn().getResponse().getHeader("Location");
        }
        mvc.perform(get(endereco).session(sessao)).andExpect(status().isNotFound());
        mvc.perform(get(ultimoEndereco).session(sessao))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "inline; filename=\"passageiros-2026-11-23.pdf\""));
    }

    @Test
    void formularioNovoExibeCamposComValoresIniciais() throws Exception {
        mvc.perform(get("/pessoas/abrirFormulario").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"formPessoaTitulo\"")))
                .andExpect(content().string(containsString("Cadastrar Pessoa")))
                .andExpect(content().string(containsString("hx-get=\"/pessoas/limpar-modal\"")))
                .andExpect(content().string(containsString("hx-post=\"/pessoas")))
                .andExpect(content().string(containsString("hx-get=\"/pessoas/buscar-especificacoes\"")))
                .andExpect(content().string(containsString("Buscar na residência")));
    }

    @Test
    void buscaEspecificacoesConformeOTermoDigitado() throws Exception {
        var especificacao = new EspecificacaoResponseDto(ESPECIFICACAO_ID, "Padrão", PRIORIDADE_ID, "Alta", null, null);
        when(especificacoes.listar("Pa", false, 0))
                .thenReturn(new ResultadoPaginado<>(List.of(especificacao), 0, 10, 1, 1));

        mvc.perform(get("/pessoas/buscar-especificacoes").param("termoEspecificacao", "Pa")
                        .header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-id=\"" + ESPECIFICACAO_ID + "\"")))
                .andExpect(content().string(containsString("Padrão (Alta)")));
        verify(especificacoes).listar("Pa", false, 0);
    }

    @Test
    void naoBuscaEspecificacoesComTermoMenorQueDuasLetras() throws Exception {
        mvc.perform(get("/pessoas/buscar-especificacoes").param("termoEspecificacao", "P")
                        .header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("<button"))));
        verify(especificacoes, never()).listar("P", false, 0);
    }

    @Test
    void exibeVeiculosFisicosDisponiveisDiretamenteAposVerificar() throws Exception {
        var data = LocalDate.of(2026, 11, 23);
        var idTipo = UUID.randomUUID();
        var idVeiculo = UUID.randomUUID();
        var destinoId = UUID.randomUUID();
        var pessoa = new PessoaResponseDto(PESSOA_ID, "Ana Souza", "", ESPECIFICACAO_ID, "Padrão",
                "Alta", PRIORIDADE_ID, 2, true, 2, false, null, "", "52998224725", "11987654321");
        var verificacao = new VerificacaoVeiculosResponseDto(
                List.of(new VeiculoPermitidoDto(idTipo, "Van", 8, List.of())), List.of());
        var veiculo = new VeiculoAbertoResponseDto(idVeiculo, data, idTipo, "Van", 8,
                "VAN-01", LocalTime.of(8, 0), 3, List.of());

        when(pessoas.buscar(PESSOA_ID)).thenReturn(pessoa);
        when(agendamentos.verificarVeiculos(eq(data), any())).thenReturn(verificacao);
        when(agendamentos.buscarDia(data))
                .thenReturn(new AgendaDiaResponseDto(data, List.of(veiculo), List.of()));
        when(listas.listarAtivas("", 0))
                .thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
        when(tiposVeiculo.listar("", 0))
                .thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
        when(localidades.listarCidades("", 0))
                .thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));

        var listaId = UUID.randomUUID();
        mvc.perform(post("/agendamentos/verificar-veiculos").header("HX-Request", "true")
                        .param("data", data.toString())
                        .param("idPessoa", PESSOA_ID.toString())
                        .param("idLista", listaId.toString())
                        .param("idDestino", destinoId.toString())
                        .param("veiculoOrigem", idVeiculo.toString())
                        .param("horarioChegada", "09:00")
                        .param("acompanhantes", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("VAN-01")))
                .andExpect(content().string(containsString("Agendar neste veículo")))
                .andExpect(content().string(containsString("Veículo selecionado")))
                .andExpect(content().string(containsString("name=\"veiculoOrigem\" value=\"" + idVeiculo + "\"")))
                .andExpect(content().string(containsString("1 acompanhante(s) selecionado(s), máximo de 2")))
                .andExpect(content().string(containsString("name=\"acompanhantes\" value=\"1\"")))
                .andExpect(content().string(containsString("hx-trigger=\"change, submit\"")))
                .andExpect(content().string(not(containsString("Tipo inicial do agendamento"))))
                .andExpect(content().string(not(containsString("veiculoFisico"))));
        verify(agendamentos).verificarVeiculos(data, new VerificarVeiculosRequestDto(
                PESSOA_ID, listaId, destinoId, LocalTime.of(9, 0), 1));

        mvc.perform(post("/agendamentos/passageiros")
                        .param("data", data.toString())
                        .param("idPessoa", PESSOA_ID.toString())
                        .param("idLista", listaId.toString())
                        .param("idTipoVeiculo", idTipo.toString())
                        .param("idDestino", destinoId.toString())
                        .param("horarioChegada", "09:00")
                        .param("buscarNaResidencia", "true")
                        .param("acompanhantes", "1")
                        .param("idVeiculo", idVeiculo.toString()))
                .andExpect(status().is3xxRedirection());
        verify(agendamentos).criar(data, new AgendamentoRequestDto(PESSOA_ID, listaId, idTipo,
                destinoId, LocalTime.of(9, 0), true, 1, idVeiculo));
    }

    @Test
    void edicaoDoAgendamentoSelecionaEEnviaQuantidadeDeAcompanhantes() throws Exception {
        var data = LocalDate.of(2026, 11, 23);
        var idAgendamento = UUID.randomUUID();
        var idTipo = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var pessoa = new PessoaResponseDto(PESSOA_ID, "Ana Souza", "", ESPECIFICACAO_ID, "Padrão",
                "Alta", PRIORIDADE_ID, 2, true, 2, false, null, "", "52998224725", "11987654321");
        var passageiro = new AgendamentoResponseDto(idAgendamento, data, PESSOA_ID, UUID.randomUUID(),
                UUID.randomUUID(), idTipo, idDestino, LocalTime.of(9, 0), LocalTime.of(8, 0),
                true, 1, null, List.of(), "Responsável pelo agendamento");
        when(pessoas.buscar(PESSOA_ID)).thenReturn(pessoa);
        when(agendamentos.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(), List.of(passageiro)));
        when(listas.listarAtivas("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
        when(tiposVeiculo.listar("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new TipoVeiculoResponseDto(idTipo, "Carro", 4)), 0, 10, 1, 1));
        when(localidades.listarCidades("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
        when(localidades.listarCidadesArquivadas("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
        when(listas.buscarArquivada(passageiro.idLista())).thenReturn(new ListaResponseDto(
                passageiro.idLista(), "Lista antiga", "", 0, 0, true, List.of(), List.of(), List.of(), List.of()));

        for (var caminho : List.of("/agendamentos", "/agendamentos/carros")) {
            mvc.perform(get(caminho).param("data", data.toString()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("editar-acompanhantes-" + idAgendamento + "-0")))
                    .andExpect(content().string(containsString("editar-acompanhantes-" + idAgendamento + "-2")))
                    .andExpect(content().string(matchesPattern("(?s).*id=\"editar-acompanhantes-"
                            + idAgendamento + "-1\"\\s+value=\"1\"\\s+required\\s+checked=\"checked\".*")));
        }
        mvc.perform(get("/agendamentos").param("data", data.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Responsável pelo agendamento")));
        var antigo = new AgendamentoResponseDto(idAgendamento, data, PESSOA_ID, passageiro.idLista(),
                passageiro.idRegraCadastro(), idTipo, idDestino, LocalTime.of(9, 0), LocalTime.of(8, 0),
                true, 1, null, List.of());
        when(agendamentos.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(), List.of(antigo)));
        mvc.perform(get("/agendamentos").param("data", data.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Autor desconhecido")));

        when(pessoas.buscar(PESSOA_ID)).thenReturn(new PessoaResponseDto(PESSOA_ID, "Ana Souza", "",
                ESPECIFICACAO_ID, "Padrão", "Alta", PRIORIDADE_ID, 0, true, 2, false,
                null, "", "52998224725", "11987654321"));
        mvc.perform(get("/agendamentos").param("data", data.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("editar-acompanhantes-atual-" + idAgendamento)))
                .andExpect(content().string(containsString("Uma nova seleção deve respeitar esse limite.")));

        mvc.perform(post("/agendamentos/{id}/editar", idAgendamento)
                        .param("data", data.toString()).param("idTipoVeiculo", idTipo.toString())
                        .param("idDestino", idDestino.toString()).param("horarioChegada", "09:00")
                        .param("buscarNaResidencia", "true").param("acompanhantes", "2"))
                .andExpect(status().is3xxRedirection());
        verify(agendamentos).atualizar(idAgendamento, new AgendamentoAtualizarRequestDto(
                idTipo, idDestino, LocalTime.of(9, 0), true, 2, null));
    }

    @Test
    void detalhesMostramAutorConhecidoOuDesconhecido() throws Exception {
        when(pessoas.buscar(PESSOA_ID)).thenReturn(new PessoaResponseDto(PESSOA_ID, "Ana Souza", "",
                ESPECIFICACAO_ID, "Padrão", "Alta", PRIORIDADE_ID, 2, false, 0, false, null,
                "", null, "11987654321", "Responsável pela pessoa"));
        mvc.perform(get("/pessoas/{id}/detalhes", PESSOA_ID).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Responsável pela pessoa")));
        when(pessoas.buscar(PESSOA_ID)).thenReturn(new PessoaResponseDto(PESSOA_ID, "Ana Souza", "",
                ESPECIFICACAO_ID, "Padrão", "Alta", PRIORIDADE_ID, 2, false, 0, false, null,
                "", null, "11987654321"));
        mvc.perform(get("/pessoas/{id}/detalhes", PESSOA_ID).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Autor desconhecido")));
    }

    @Test
    void paginaExibeCabecalhoEConservaAcoesHtmx() throws Exception {
        mvc.perform(get("/pessoas"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h1>Pessoas</h1>")))
                .andExpect(content().string(containsString("hx-get=\"/pessoas/abrirFormulario\"")))
                .andExpect(content().string(matchesPattern(
                        "(?s).*hx-get=\"/pessoas/listar\\?arquivadas=true\"\\s+hx-target=\"#modal\".*")));
    }

    @Test
    void cadastraPessoaComCamposPadraoEAtualizaLista() throws Exception {
        var dto = new PessoaRequestDto("Ana Souza", "", ESPECIFICACAO_ID, "", "52998224725", "11987654321", true);
        when(pessoas.salvar(dto)).thenReturn(PESSOA_ID);

        mvc.perform(post("/pessoas").header("HX-Request", "true")
                        .param("nome", dto.nome()).param("observacao", "")
                        .param("especificacaoId", ESPECIFICACAO_ID.toString())
                        .param("endereco", "").param("cpf", dto.cpf()).param("telefone", dto.telefone())
                        .param("buscarNaResidencia", "true"))
                .andExpect(status().isOk())
                .andExpect(header().string("HX-Trigger-After-Settle", containsString("atualizar-pessoas")));
        verify(pessoas).salvar(dto);
    }

    @Test
    void edicaoMostraEEnviaOsValoresExistentes() throws Exception {
        var pessoa = new PessoaResponseDto(PESSOA_ID, "Ana Souza", "", ESPECIFICACAO_ID, "Padrão",
                "Alta", PRIORIDADE_ID, 2, true, 2, false, null, "", "52998224725", "11987654321");
        when(pessoas.buscar(PESSOA_ID)).thenReturn(pessoa);

        mvc.perform(get("/pessoas/abrirFormulario/{id}", PESSOA_ID).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Editar Pessoa")))
                .andExpect(content().string(containsString("hx-put=\"/pessoas/" + PESSOA_ID + "?contextoAgendamento=false\"")))
                .andExpect(content().string(not(containsString("name=\"quantidadeAcompanhantes\""))))
                .andExpect(content().string(containsString("name=\"buscarNaResidencia\"")));

        var dto = new PessoaRequestDto("Ana Souza", "", ESPECIFICACAO_ID, "", "52998224725", "11987654321", true);
        mvc.perform(put("/pessoas/{id}", PESSOA_ID).header("HX-Request", "true")
                        .param("nome", dto.nome()).param("observacao", "")
                        .param("especificacaoId", ESPECIFICACAO_ID.toString())
                        .param("endereco", "").param("cpf", dto.cpf()).param("telefone", dto.telefone())
                        .param("buscarNaResidencia", "true"))
                .andExpect(status().isOk())
                .andExpect(header().string("HX-Trigger-After-Settle", "atualizar-pessoas"));
        verify(pessoas).editar(PESSOA_ID, dto);
    }

    @Test
    void edicaoNoAgendamentoAtualizaAPaginaSomenteAposSalvar() throws Exception {
        var pessoa = new PessoaResponseDto(PESSOA_ID, "Ana Souza", "", ESPECIFICACAO_ID, "Padrão",
                "Alta", PRIORIDADE_ID, 2, true, 2, false, null, "", "52998224725", "11987654321");
        when(pessoas.buscar(PESSOA_ID)).thenReturn(pessoa);
        mvc.perform(get("/pessoas/abrirFormulario/{id}", PESSOA_ID)
                        .param("contextoAgendamento", "true").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("?contextoAgendamento=true")));
        mvc.perform(put("/pessoas/{id}", PESSOA_ID).header("HX-Request", "true")
                        .param("contextoAgendamento", "true")
                        .param("nome", "Ana Souza").param("especificacaoId", ESPECIFICACAO_ID.toString())
                        .param("telefone", "11987654321").param("cpf", "52998224725")
                        .param("_buscarNaResidencia", "on"))
                .andExpect(status().isOk())
                .andExpect(header().string("HX-Refresh", "true"));
        mvc.perform(put("/pessoas/{id}", PESSOA_ID).header("HX-Request", "true")
                        .param("contextoAgendamento", "true").param("nome", ""))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("HX-Refresh"))
                .andExpect(header().doesNotExist("HX-Trigger-After-Settle"));
    }

    @Test
    void listaPessoasComPrioridadeDaEspecificacao() throws Exception {
        prepararEspecificacoesFiltro();
        var pessoa = new PessoaResponseDto(PESSOA_ID, "Ana Souza", "", ESPECIFICACAO_ID, "Padrão",
                "Alta", PRIORIDADE_ID, 2, true, 2, false, null, "", "52998224725", "11987654321");
        when(pessoas.listar("", false, 0))
                .thenReturn(new ResultadoPaginado<>(List.of(pessoa), 0, 10, 1, 1));
        mvc.perform(get("/pessoas/listar").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ana Souza")))
                .andExpect(content().string(containsString("id=\"inputBuscaPessoa\"")))
                .andExpect(content().string(containsString("hx-preserve=\"true\"")))
                .andExpect(content().string(containsString("badge text-bg-secondary")));
    }

    @Test
    void apresentaPessoaCadastradaNaBuscaDeAgendamento() throws Exception {
        var pessoa = new PessoaResponseDto(PESSOA_ID, "Ana Souza", "", ESPECIFICACAO_ID, "Padrão",
                "Alta", PRIORIDADE_ID, 2, true, 2, false, null, "", "52998224725", "11987654321");
        when(pessoas.listar("Ana", false, 0))
                .thenReturn(new ResultadoPaginado<>(List.of(pessoa), 0, 10, 1, 1));
        mvc.perform(get("/agendamentos/buscar-pessoas").param("termo", "Ana").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-prioridade=\"Alta\"")));
    }

    @Test
    void apresentaDetalhesEListaArquivada() throws Exception {
        prepararEspecificacoesFiltro();
        var pessoa = new PessoaResponseDto(PESSOA_ID, "Ana Souza", "", ESPECIFICACAO_ID, "Padrão",
                "Alta", PRIORIDADE_ID, 2, true, 2, false, LocalDate.of(2026, 1, 15), "", "52998224725", "11987654321");
        when(pessoas.listar("Ana", true, 1))
                .thenReturn(new ResultadoPaginado<>(List.of(pessoa), 1, 10, 30, 3));
        when(pessoas.buscarArquivado(PESSOA_ID)).thenReturn(pessoa);

        mvc.perform(get("/pessoas/listar").param("arquivadas", "true").param("nome", "Ana")
                        .param("pagina", "1").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("15/01/2026")))
                .andExpect(content().string(containsString("aria-labelledby=\"pessoasArquivadasTitulo\"")))
                .andExpect(content().string(containsString("atualizar-pessoas from:body")))
                .andExpect(content().string(containsString("nome=Ana&amp;arquivadas=true")))
                .andExpect(content().string(containsString("pagina=2")))
                .andExpect(content().string(containsString("hx-target=\"#modal\"")))
                .andExpect(content().string(containsString("/pessoas/" + PESSOA_ID + "/desarquivar")));
        mvc.perform(get("/pessoas/{id}/detalhes/arquivado", PESSOA_ID)
                        .param("nome", "Ana").param("pagina", "1").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ana Souza")))
                .andExpect(content().string(containsString("/pessoas/listar?arquivadas=true&amp;nome=Ana&amp;pagina=1")))
                .andExpect(content().string(not(containsString("/pessoas/abrirFormulario/"))));
        mvc.perform(patch("/pessoas/{id}/desarquivar", PESSOA_ID).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(header().string("HX-Trigger-After-Settle", "atualizar-pessoas"));
        verify(pessoas).desarquivar(PESSOA_ID);
    }

    private void prepararEspecificacoesFiltro() {
        when(especificacoes.listar("", false, 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new EspecificacaoResponseDto(ESPECIFICACAO_ID, "Padrão", PRIORIDADE_ID, "Alta", "", null)),
                0, 10, 1, 1));
        when(especificacoes.listar("", true, 0))
                .thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
    }

    @Test
    void filtraPessoasPorEspecificacaoESaltaPaginas() throws Exception {
        prepararEspecificacoesFiltro();
        when(pessoas.listarPorEspecificacao("Ana", ESPECIFICACAO_ID, false, 3))
                .thenReturn(new ResultadoPaginado<>(List.of(), 3, 10, 90, 9));

        mvc.perform(get("/pessoas/listar").header("HX-Request", "true")
                        .param("nome", "Ana").param("especificacaoId", ESPECIFICACAO_ID.toString())
                        .param("pagina", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"filtroEspecificacaoPessoa\"")))
                .andExpect(content().string(containsString("especificacaoId=" + ESPECIFICACAO_ID)))
                .andExpect(content().string(containsString("pagina=0")))
                .andExpect(content().string(containsString("pagina=8")))
                .andExpect(content().string(containsString("aria-current=\"page\"")));
    }

    @Test
    void preservaFiltroAoVoltarDosDetalhesDePessoaArquivada() throws Exception {
        var pessoa = new PessoaResponseDto(PESSOA_ID, "Ana Souza", "", ESPECIFICACAO_ID, "Padrão",
                "Alta", PRIORIDADE_ID, 2, true, 2, false, LocalDate.of(2026, 1, 15), "", "52998224725", "11987654321");
        when(pessoas.buscarArquivado(PESSOA_ID)).thenReturn(pessoa);

        mvc.perform(get("/pessoas/{id}/detalhes/arquivado", PESSOA_ID)
                        .param("nome", "Ana").param("pagina", "1")
                        .param("especificacaoId", ESPECIFICACAO_ID.toString()).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("especificacaoId=" + ESPECIFICACAO_ID)))
                .andExpect(content().string(containsString("pagina=1")));
    }

    @Test
    @WithMockUser(roles = {"AGENDAMENTO", "MOTORISTA"})
    void paginaDeCarrosOfereceCadastroEAbreInsercaoAposCriacao() throws Exception {
        var data = LocalDate.of(2026, 11, 23);
        var idTipo = UUID.randomUUID();
        var idVeiculo = UUID.randomUUID();
        var veiculo = new VeiculoAbertoResponseDto(idVeiculo, data, idTipo, "Carro", 4,
                "Carro 01", null, 0, List.of());
        var semIdentificacao = new VeiculoAbertoResponseDto(UUID.randomUUID(), data, idTipo, "Carro", 4,
                null, null, 0, List.of());
        when(agendamentos.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(veiculo, semIdentificacao), List.of()));
        when(listas.listarAtivas("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
        when(tiposVeiculo.listar("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new TipoVeiculoResponseDto(idTipo, "Carro", 4)), 0, 10, 1, 1));
        when(localidades.listarCidades("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));

        mvc.perform(get("/agendamentos/carros").param("data", data.toString())
                        .flashAttr("veiculoParaInsercao", idVeiculo))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-modal-id=\"atribuirPassageiroCarroModal-" + idVeiculo + "\"")))
                .andExpect(content().string(containsString("Cadastrar passageiro")))
                .andExpect(content().string(containsString("veiculoOrigem=" + idVeiculo)))
                .andExpect(content().string(containsString("origemCarros=true")))
                .andExpect(content().string(containsString("Sem motorista")))
                .andExpect(content().string(containsString("border-warning border-2")))
                .andExpect(content().string(containsString("Carro 01")))
                .andExpect(content().string(not(containsString("Sem identificação"))))
                .andExpect(content().string(not(containsString("id=\"identificacaoCarro\""))))
                .andExpect(content().string(containsString("/motoristas/selecionar/" + idVeiculo)));

        when(motoristas.atribuicoes(data)).thenReturn(List.of(
                new MotoristaApi.Atribuicao(idVeiculo, UUID.randomUUID(), "Motorista Ana")));
        mvc.perform(get("/agendamentos/carros").param("data", data.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Motorista: Motorista Ana")))
                .andExpect(content().string(containsString("border-success border-2")));

        mvc.perform(get("/agendamentos").param("data", data.toString())
                        .param("novoPassageiro", "true").param("veiculoOrigem", idVeiculo.toString())
                        .param("origemCarros", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-abrir-ao-carregar=\"true\"")))
                .andExpect(content().string(containsString("name=\"veiculoOrigem\" value=\"" + idVeiculo + "\"")))
                .andExpect(content().string(containsString("Buscar histórico do passageiro")));
    }

    @Test
    @WithMockUser(roles = {"AGENDAMENTO", "MOTORISTA"})
    void paginaDeVeiculosDistingueMotoristasAtribuidosEIndisponibilidadeDaConsulta() throws Exception {
        var data = LocalDate.of(2026, 11, 23);
        var tipo = UUID.randomUUID();
        var primeiro = UUID.randomUUID();
        var segundo = UUID.randomUUID();
        var van1 = new VeiculoAbertoResponseDto(primeiro, data, tipo, "Van", 8, "VAN-01", null, 0, List.of());
        var van2 = new VeiculoAbertoResponseDto(segundo, data, tipo, "Van", 8, "VAN-02", null, 0, List.of());
        when(agendamentos.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(van1, van2), List.of()));
        when(listas.listarAtivas("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
        when(tiposVeiculo.listar("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new TipoVeiculoResponseDto(tipo, "Van", 8)), 0, 10, 1, 1));
        when(localidades.listarCidades("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
        when(motoristas.atribuicoes(data)).thenReturn(List.of(
                new MotoristaApi.Atribuicao(primeiro, UUID.randomUUID(), "Ana")));
        mvc.perform(get("/agendamentos").param("data", data.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Motorista: Ana")))
                .andExpect(content().string(containsString("Sem motorista")))
                .andExpect(content().string(containsString("border-warning border-2")))
                .andExpect(content().string(containsString("border-success border-2")))
                .andExpect(content().string(containsString("/motoristas/selecionar/" + segundo)));
        when(motoristas.atribuicoes(data)).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        mvc.perform(get("/agendamentos").param("data", data.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Não foi possível carregar motoristas")))
                .andExpect(content().string(containsString("Motorista indisponível")))
                .andExpect(content().string(not(containsString("Sem motorista"))));
    }

    @Test
    void historicoNoAgendamentoMantemBuscaEPaginacaoNoSeuContainer() throws Exception {
        var viagem = new HistoricoViagemResponseDto(UUID.randomUUID(), LocalDate.of(2026, 11, 2),
                LocalTime.of(9, 0), LocalTime.of(8, 0), "Hospital", "Consulta", "Van", true, 1);
        when(historico.buscarHistoricoViagens(PESSOA_ID, 0))
                .thenReturn(new ResultadoPaginado<>(List.of(viagem), 0, 10, 11, 2));
        when(historico.buscarHistoricoViagensComFiltro(PESSOA_ID, "Hospital", 1))
                .thenReturn(new ResultadoPaginado<>(List.of(viagem), 1, 10, 11, 2));

        mvc.perform(get("/pessoas/{id}/historico-viagens", PESSOA_ID)
                        .param("contextoAgendamento", "true").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hx-target=\"#historico-agendamento-container\"")))
                .andExpect(content().string(containsString("hx-include=\"#destinoBuscaAgendamento\"")))
                .andExpect(content().string(containsString("contextoAgendamento=true")))
                .andExpect(content().string(containsString("02/11/2026")));
        mvc.perform(get("/pessoas/{id}/historico-viagens/buscar", PESSOA_ID)
                        .param("contextoAgendamento", "true").param("destinoBusca", "Hospital")
                        .param("pagina", "1").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("value=\"Hospital\"")))
                .andExpect(content().string(containsString("hx-target=\"#historico-agendamento-container\"")));
    }

    @Test
    void aberturaDeCarroPreparaInsercaoNoVeiculoCriado() throws Exception {
        var data = LocalDate.of(2026, 11, 23);
        var idTipo = UUID.randomUUID();
        var idVeiculo = UUID.randomUUID();
        when(tiposVeiculo.buscar(idTipo)).thenReturn(new TipoVeiculoResponseDto(idTipo, "Carro", 4));
        when(agendamentos.abrirVeiculo(eq(data), any())).thenReturn(
                new VeiculoAbertoResponseDto(idVeiculo, data, idTipo, "Carro", 4,
                        null, null, 0, List.of()));

        mvc.perform(post("/agendamentos/carros/veiculos").param("data", data.toString())
                        .param("idTipoVeiculo", idTipo.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamentos/carros?data=" + data))
                .andExpect(flash().attribute("veiculoParaInsercao", idVeiculo));
        verify(agendamentos).abrirVeiculo(data, new AbrirVeiculoRequestDto(idTipo, null, null));
    }

    @Test
    void cadastroDePassageiroDeCarroRetornaAInsercaoNoCarroDeOrigem() throws Exception {
        var data = LocalDate.of(2026, 11, 23);
        var idTipo = UUID.randomUUID();
        var idLista = UUID.randomUUID();
        var idDestino = UUID.randomUUID();
        var idVeiculo = UUID.randomUUID();

        mvc.perform(post("/agendamentos/passageiros").param("data", data.toString())
                        .param("idPessoa", PESSOA_ID.toString()).param("idLista", idLista.toString())
                        .param("idTipoVeiculo", idTipo.toString()).param("idDestino", idDestino.toString())
                        .param("horarioChegada", "09:00").param("veiculoOrigem", idVeiculo.toString())
                        .param("origemCarros", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamentos/carros?data=" + data))
                .andExpect(flash().attribute("veiculoParaInsercao", idVeiculo))
                .andExpect(flash().attribute("pessoaParaInsercao", PESSOA_ID));
        verify(agendamentos).criar(data, new AgendamentoRequestDto(PESSOA_ID, idLista, idTipo,
                idDestino, LocalTime.of(9, 0), false, 0, null));
    }

    @Test
    void historicoAbreEAtualizaBuscaEPaginacaoNoMesmoContainer() throws Exception {
        var pessoa = new PessoaResponseDto(PESSOA_ID, "Ana Souza", "", ESPECIFICACAO_ID, "Padrão",
                "Alta", PRIORIDADE_ID, 2, true, 2, false, null, "", "52998224725", "11987654321");
        var viagem = new HistoricoViagemResponseDto(UUID.randomUUID(), LocalDate.of(2026, 11, 2),
                LocalTime.of(9, 0), LocalTime.of(8, 0), "Hospital", "Consulta", "Van", true, 1);
        when(pessoas.buscar(PESSOA_ID)).thenReturn(pessoa);
        when(historico.buscarHistoricoViagens(PESSOA_ID, 0))
                .thenReturn(new ResultadoPaginado<>(List.of(viagem), 0, 10, 11, 2));
        when(historico.buscarHistoricoViagensComFiltro(PESSOA_ID, "Hospital", 1))
                .thenReturn(new ResultadoPaginado<>(List.of(viagem), 1, 10, 11, 2));

        mvc.perform(get("/pessoas/{id}/detalhes", PESSOA_ID).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hx-get=\"/pessoas/" + PESSOA_ID + "/historico-viagens\"")));
        mvc.perform(get("/pessoas/{id}/historico-viagens", PESSOA_ID).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("02/11/2026")))
                .andExpect(content().string(containsString("08:00")))
                .andExpect(content().string(containsString("hx-get=\"/pessoas/" + PESSOA_ID + "/historico-viagens/buscar\"")))
                .andExpect(content().string(containsString("hx-target=\"#historico-viagens-container\"")))
                .andExpect(content().string(containsString("hx-include=\"#destinoBusca\"")))
                .andExpect(content().string(containsString("/historico-viagens/buscar?pagina=1")));
        mvc.perform(get("/pessoas/{id}/historico-viagens/buscar", PESSOA_ID)
                        .param("destinoBusca", "Hospital").param("pagina", "1").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("value=\"Hospital\"")))
                .andExpect(content().string(containsString("Hospital")));
    }

    @Test
    void confirmaValidacaoEExibePessoaValidada() throws Exception {
        var pessoa = new PessoaResponseDto(PESSOA_ID, "Ana Souza", "", ESPECIFICACAO_ID, "Padrão",
                "Alta", PRIORIDADE_ID, 2, true, 2, true, null, "", "52998224725", "11987654321");
        when(pessoas.buscar(PESSOA_ID)).thenReturn(pessoa);

        mvc.perform(patch("/pessoas/{id}/validar", PESSOA_ID).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Dados validados")))
                .andExpect(content().string(not(containsString("Confirmar validação"))));

        verify(pessoas).validar(PESSOA_ID);
    }
}
