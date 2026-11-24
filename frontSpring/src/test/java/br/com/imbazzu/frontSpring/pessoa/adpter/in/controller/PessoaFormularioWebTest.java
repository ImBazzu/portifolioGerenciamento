package br.com.imbazzu.frontSpring.pessoa.adpter.in.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.agendamento.core.dto.HistoricoViagemResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoPermitidoDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificacaoVeiculosResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificarVeiculosRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoAtualizarRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.porta.in.HistoricoViagemApplicationPorta;
import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoResponseDto;
import br.com.imbazzu.frontSpring.especificacao.core.porta.in.EspecificacaoApplicationPorta;
import static org.hamcrest.Matchers.not;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaRequestDto;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;

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
    private ListaApplicationPorta listas;

    @MockitoBean
    private LocalidadeApplicationPorta localidades;

    @MockitoBean
    private VeiculoApplicationPorta tiposVeiculo;

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
                        .param("horarioChegada", "09:00")
                        .param("acompanhantes", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("VAN-01")))
                .andExpect(content().string(containsString("Agendar neste veículo")))
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
                true, 1, null, List.of());
        when(pessoas.buscar(PESSOA_ID)).thenReturn(pessoa);
        when(agendamentos.buscarDia(data)).thenReturn(new AgendaDiaResponseDto(data, List.of(), List.of(passageiro)));
        when(listas.listarAtivas("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));
        when(tiposVeiculo.listar("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(new TipoVeiculoResponseDto(idTipo, "Carro", 4)), 0, 10, 1, 1));
        when(localidades.listarCidades("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 10, 0, 0));

        for (var caminho : List.of("/agendamentos", "/agendamentos/carros")) {
            mvc.perform(get(caminho).param("data", data.toString()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("editar-acompanhantes-" + idAgendamento + "-0")))
                    .andExpect(content().string(containsString("editar-acompanhantes-" + idAgendamento + "-2")))
                    .andExpect(content().string(matchesPattern("(?s).*id=\"editar-acompanhantes-"
                            + idAgendamento + "-1\"\\s+value=\"1\"\\s+required\\s+checked=\"checked\".*")));
        }

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
                .andExpect(content().string(containsString("nome=Ana&amp;arquivadas=true&amp;pagina=2")))
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
