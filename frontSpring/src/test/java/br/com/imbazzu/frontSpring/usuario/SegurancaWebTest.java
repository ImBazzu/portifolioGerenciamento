package br.com.imbazzu.frontSpring.usuario;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import br.com.imbazzu.frontSpring.autenticacao.core.exception.RefreshTokenInvalidoException;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.SessionAuthPorta;
import br.com.imbazzu.frontSpring.prioridade.PrioridadeApi;
import br.com.imbazzu.frontSpring.relatorio.controller.InvestigacaoApi;

@SpringBootTest
@AutoConfigureMockMvc
class SegurancaWebTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private UsuarioApi usuarios;
    @MockitoBean private SessionAuthPorta sessao;
    @MockitoBean private PrioridadeApi prioridades;
    @MockitoBean private InvestigacaoApi investigacao;
    private final UUID id = UUID.randomUUID();

    @BeforeEach
    void preparar() {
        when(sessao.access()).thenReturn("token");
        when(prioridades.listar()).thenReturn(List.of());
    }

    @Test
    void aplicaPerfilAtualNoMenuENasRotas() throws Exception {
        when(usuarios.proprio()).thenReturn(new UsuarioApi.Usuario(id, "Ana", "AGENDAMENTO"));
        var conta = mvc.perform(get("/minha-conta")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Ana")))
                .andExpect(content().string(containsString("data-bs-target=\"#editarContaModal\"")))
                .andExpect(content().string(not(containsString("href=\"/usuarios\""))))
                .andExpect(content().string(not(containsString("href=\"/motoristas\""))))
                .andReturn().getResponse().getContentAsString();
        assertTrue(conta.indexOf("id=\"editarContaModal\"") < conta.indexOf("id=\"nomeConta\""));
        mvc.perform(get("/motoristas")).andExpect(status().isForbidden());
        mvc.perform(get("/prioridades")).andExpect(status().isForbidden());
        mvc.perform(get("/relatorios/investigacao/pessoas")).andExpect(status().isForbidden());
        mvc.perform(get("/relatorios/investigacao/motoristas")).andExpect(status().isForbidden());
        mvc.perform(get("/usuarios")).andExpect(status().isForbidden());

        when(usuarios.proprio()).thenReturn(new UsuarioApi.Usuario(id, "Ana", "GESTAO"));
        when(investigacao.pessoas("", 0)).thenReturn(new InvestigacaoApi.Pagina<>(List.of(), 0, 0, 0));
        mvc.perform(get("/prioridades")).andExpect(status().isOk());
        mvc.perform(get("/relatorios/investigacao/pessoas")).andExpect(status().isOk());
        mvc.perform(get("/usuarios")).andExpect(status().isForbidden());

        when(usuarios.proprio()).thenReturn(new UsuarioApi.Usuario(id, "Ana", "ADMIN"));
        when(usuarios.listar("", 0)).thenReturn(new UsuarioApi.Pagina(
                List.of(new UsuarioApi.Usuario(id, "Ana", "ADMIN")), 1, 0));
        var pagina = mvc.perform(get("/usuarios").flashAttr("novoNome", "Beatriz")
                        .flashAttr("novoPerfil", "GESTAO")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Usuários e permissões")))
                .andExpect(content().string(containsString("Ana")))
                .andExpect(content().string(containsString("data-bs-target=\"#novoUsuarioModal\"")))
                .andExpect(content().string(containsString("data-bs-target=\"#editarPerfil-" + id + "\"")))
                .andReturn().getResponse().getContentAsString();
        assertTrue(pagina.indexOf("id=\"novoUsuarioModal\"") < pagina.indexOf("id=\"novoNome\""));
        assertTrue(pagina.indexOf("id=\"editarPerfil-" + id + "\"") < pagina.indexOf("id=\"perfil-" + id + "\""));
        assertTrue(pagina.contains("value=\"Beatriz\""));
        assertTrue(pagina.contains("value=\"GESTAO\" selected=\"selected\""));
        mvc.perform(post("/minha-conta").with(csrf()).param("nome", "Ana nova")
                .param("senhaAtual", "senha123").param("novaSenha", "outrasenha123"))
                .andExpect(redirectedUrl("/minha-conta"));
        verify(usuarios).atualizarProprio("Ana nova", "senha123", "outrasenha123");
    }

    @Test
    void sessaoInvalidaRedirecionaParaLogin() throws Exception {
        when(usuarios.proprio()).thenThrow(new RefreshTokenInvalidoException());
        mvc.perform(get("/minha-conta")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(sessao).clear();
    }

    @Test
    void bloqueiaValidacaoParaPerfisInferioresEApenasAdminGerenciaArquivados() throws Exception {
        when(usuarios.proprio()).thenReturn(new UsuarioApi.Usuario(id, "Ana", "MOTORISTA"));
        mvc.perform(patch("/pessoas/{id}/validar", id).with(csrf())).andExpect(status().isForbidden());
        mvc.perform(patch("/pessoas/{id}/retirar-validacao", id).with(csrf())).andExpect(status().isForbidden());
        mvc.perform(post("/usuarios/{id}/situacao/excluir", id).with(csrf())).andExpect(status().isForbidden());

        when(usuarios.proprio()).thenReturn(new UsuarioApi.Usuario(id, "Ana", "ADMIN"));
        var outro = UUID.randomUUID();
        when(usuarios.listar("", true, 0)).thenReturn(new UsuarioApi.Pagina(
                List.of(new UsuarioApi.Usuario(outro, "Arquivado", "AGENDAMENTO")), 1, 0));
        mvc.perform(get("/usuarios").param("arquivados", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Usuários arquivados")))
                .andExpect(content().string(containsString("/usuarios/" + outro + "/situacao/reativar")));
        mvc.perform(post("/usuarios/{id}/situacao/reativar", outro).with(csrf()).param("arquivados", "true"))
                .andExpect(redirectedUrl("/usuarios?arquivados=true"));
        verify(usuarios).alterarSituacao(outro, "reativar");
    }
}
