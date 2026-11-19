package br.com.imbazzu.frontSpring.pessoa.adpter.in.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoResponseDto;
import br.com.imbazzu.frontSpring.especificacao.core.porta.in.EspecificacaoApplicationPorta;
import br.com.imbazzu.frontSpring.pessoa.core.dto.EnumPrioridade;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaRequestDto;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PessoaFormularioWebTest {
    private static final UUID ESPECIFICACAO_ID = UUID.fromString("c5d0a150-2d80-4f0b-9a1d-3cfb8d8a0f22");
    private static final UUID PESSOA_ID = UUID.fromString("b4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f21");

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PessoaApplicationPorta pessoas;

    @MockitoBean
    private EspecificacaoApplicationPorta especificacoes;

    @BeforeEach
    void configurar() {
        var especificacao = new EspecificacaoResponseDto(ESPECIFICACAO_ID, "Padrão", EnumPrioridade.ALTA, null, null);
        when(especificacoes.listar("", false, 0))
                .thenReturn(new ResultadoPaginado<>(List.of(especificacao), 0, 10, 1, 1));
    }

    @Test
    void formularioNovoExibeCamposComValoresIniciais() throws Exception {
        mvc.perform(get("/pessoas/abrirFormulario").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"buscarNaResidencia\"")))
                .andExpect(content().string(containsString("name=\"quantidadeAcompanhantes\" value=\"0\"")));
    }

    @Test
    void cadastraPessoaComCamposPadraoEAtualizaLista() throws Exception {
        var dto = new PessoaRequestDto("Ana Souza", "", ESPECIFICACAO_ID, false, 0, "", "52998224725", "11987654321");
        when(pessoas.salvar(dto)).thenReturn(PESSOA_ID);

        mvc.perform(post("/pessoas").header("HX-Request", "true")
                        .param("nome", dto.nome()).param("observacao", "")
                        .param("especificacaoId", ESPECIFICACAO_ID.toString())
                        .param("_buscarNaResidencia", "on").param("quantidadeAcompanhantes", "0")
                        .param("endereco", "").param("cpf", dto.cpf()).param("telefone", dto.telefone()))
                .andExpect(status().isOk())
                .andExpect(header().string("HX-Trigger-After-Settle", containsString("atualizar-pessoas")));
        verify(pessoas).salvar(dto);
    }

    @Test
    void edicaoMostraEEnviaOsValoresExistentes() throws Exception {
        var pessoa = new PessoaResponseDto(PESSOA_ID, "Ana Souza", "", ESPECIFICACAO_ID, "Padrão",
                EnumPrioridade.ALTA, true, 2, null, "", "52998224725", "11987654321");
        when(pessoas.buscar(PESSOA_ID)).thenReturn(pessoa);

        mvc.perform(get("/pessoas/abrirFormulario/{id}", PESSOA_ID).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("value=\"2\"")))
                .andExpect(content().string(containsString("checked=\"checked\"")));

        var dto = new PessoaRequestDto("Ana Souza", "", ESPECIFICACAO_ID, true, 2, "", "52998224725", "11987654321");
        mvc.perform(put("/pessoas/{id}", PESSOA_ID).header("HX-Request", "true")
                        .param("nome", dto.nome()).param("observacao", "")
                        .param("especificacaoId", ESPECIFICACAO_ID.toString())
                        .param("buscarNaResidencia", "true").param("_buscarNaResidencia", "on")
                        .param("quantidadeAcompanhantes", "2")
                        .param("endereco", "").param("cpf", dto.cpf()).param("telefone", dto.telefone()))
                .andExpect(status().isOk());
        verify(pessoas).editar(PESSOA_ID, dto);
    }

    @Test
    void rejeitaQuantidadeNegativa() throws Exception {
        var dto = new PessoaRequestDto("Ana Souza", "", ESPECIFICACAO_ID, false, -1, "", "52998224725", "11987654321");
        mvc.perform(post("/pessoas").header("HX-Request", "true")
                        .param("nome", dto.nome()).param("observacao", "")
                        .param("especificacaoId", ESPECIFICACAO_ID.toString())
                        .param("_buscarNaResidencia", "on").param("quantidadeAcompanhantes", "-1")
                        .param("endereco", "").param("cpf", dto.cpf()).param("telefone", dto.telefone()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("is-invalid")));
        verify(pessoas, never()).salvar(dto);
    }

    @Test
    void listaPessoasComPrioridadeDaEspecificacao() throws Exception {
        var pessoa = new PessoaResponseDto(PESSOA_ID, "Ana Souza", "", ESPECIFICACAO_ID, "Padrão",
                EnumPrioridade.ALTA, true, 2, null, "", "52998224725", "11987654321");
        when(pessoas.listar("", false, 0))
                .thenReturn(new ResultadoPaginado<>(List.of(pessoa), 0, 10, 1, 1));
        mvc.perform(get("/pessoas/listar").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ana Souza")))
                .andExpect(content().string(containsString("badge text-bg-warning text-dark")));
    }

    @Test
    void apresentaPessoaCadastradaNaBuscaDeAgendamento() throws Exception {
        var pessoa = new PessoaResponseDto(PESSOA_ID, "Ana Souza", "", ESPECIFICACAO_ID, "Padrão",
                EnumPrioridade.ALTA, true, 2, null, "", "52998224725", "11987654321");
        when(pessoas.listar("Ana", false, 0))
                .thenReturn(new ResultadoPaginado<>(List.of(pessoa), 0, 10, 1, 1));
        mvc.perform(get("/agendamentos/buscar-pessoas").param("termo", "Ana").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-prioridade=\"Alta\"")));
    }

    @Test
    void apresentaDetalhesEListaArquivada() throws Exception {
        var pessoa = new PessoaResponseDto(PESSOA_ID, "Ana Souza", "", ESPECIFICACAO_ID, "Padrão",
                EnumPrioridade.ALTA, true, 2, LocalDate.of(2026, 1, 15), "", "52998224725", "11987654321");
        when(pessoas.listar("", true, 0))
                .thenReturn(new ResultadoPaginado<>(List.of(pessoa), 0, 10, 1, 1));
        when(pessoas.buscarArquivado(PESSOA_ID)).thenReturn(pessoa);

        mvc.perform(get("/pessoas/listar").param("arquivadas", "true").header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("15/01/2026")));
        mvc.perform(get("/pessoas/{id}/detalhes/arquivado", PESSOA_ID).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ana Souza")));
    }
}
