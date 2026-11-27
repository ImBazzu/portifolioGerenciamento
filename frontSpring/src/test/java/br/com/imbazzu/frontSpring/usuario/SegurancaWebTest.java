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
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
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
        mvc.perform(get("/usuarios/listas-agendamento/{id}", id).header("HX-Request", "true"))
                .andExpect(status().isForbidden());

        when(usuarios.proprio()).thenReturn(new UsuarioApi.Usuario(id, "Ana", "GESTAO"));
        when(investigacao.pessoas("", 0)).thenReturn(new InvestigacaoApi.Pagina<>(List.of(), 0, 0, 0));
        when(usuarios.listarParaVisibilidadeListas())
                .thenReturn(List.of(new UsuarioApi.Usuario(id, "Ana", "GESTAO")));
        mvc.perform(get("/prioridades")).andExpect(status().isOk());
        mvc.perform(get("/relatorios/investigacao/pessoas")).andExpect(status().isOk());
        mvc.perform(get("/usuarios")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Usuários do agendamento")))
                .andExpect(content().string(containsString("Listas visíveis")));

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
    void gestaoConfiguraListasDeUmUsuario() throws Exception {
        var operador = new UsuarioApi.Usuario(UUID.randomUUID(), "Operador", "AGENDAMENTO");
        var lista = UUID.randomUUID();
        when(usuarios.proprio()).thenReturn(new UsuarioApi.Usuario(id, "Gestor", "GESTAO"));
        when(usuarios.listarParaVisibilidadeListas()).thenReturn(List.of(operador));
        when(usuarios.listasAgendamento(operador.id())).thenReturn(new UsuarioApi.VisibilidadeListas(
                true, List.of(lista), List.of(new UsuarioApi.ListaAgendamento(lista, "Emergência"))));

        mvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hx-get=\"/usuarios/listas-agendamento/" + operador.id() + "\"")));
        mvc.perform(get("/usuarios/listas-agendamento/{id}", operador.id()).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("role=\"dialog\"")))
                .andExpect(content().string(containsString("Configurando:")))
                .andExpect(content().string(containsString("Operador")))
                .andExpect(content().string(containsString("Emergência")))
                .andExpect(content().string(containsString("checked=\"checked\"")));
        mvc.perform(get("/usuarios").param("usuarioListas", operador.id().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Configurando:")))
                .andExpect(content().string(not(containsString("hx-get=\"/usuarios/listas-agendamento/fechar\""))));
        mvc.perform(get("/usuarios/listas-agendamento/fechar").header("HX-Request", "true"))
                .andExpect(status().isOk()).andExpect(content().string(""));
        mvc.perform(post("/usuarios/listas-agendamento/{id}", operador.id()).with(csrf())
                        .param("restrita", "true").param("idsListas", lista.toString()))
                .andExpect(redirectedUrl("/usuarios"));
        verify(usuarios).atualizarListasAgendamento(operador.id(), true, List.of(lista));
    }

    @Test
    void erroAoCarregarListasApareceNoModal() throws Exception {
        var operador = new UsuarioApi.Usuario(UUID.randomUUID(), "Operador", "AGENDAMENTO");
        when(usuarios.proprio()).thenReturn(new UsuarioApi.Usuario(id, "Gestor", "GESTAO"));
        when(usuarios.listarParaVisibilidadeListas()).thenReturn(List.of(operador));
        when(usuarios.listasAgendamento(operador.id()))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        mvc.perform(get("/usuarios/listas-agendamento/{id}", operador.id()).header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("role=\"dialog\"")))
                .andExpect(content().string(containsString("alert-danger")))
                .andExpect(content().string(not(containsString("Salvar configuração"))));
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
