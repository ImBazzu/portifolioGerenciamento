package br.com.imbazzu.frontSpring.usuario;

import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MensagemErroApi;

@Controller
public class UsuarioPaginaController {
    private final UsuarioApi usuarios;
    private final MensagemErroApi erro;

    public UsuarioPaginaController(UsuarioApi usuarios, MensagemErroApi erro) {
        this.usuarios = usuarios;
        this.erro = erro;
    }

    @GetMapping("/usuarios")
    public String listar(@RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "false") boolean arquivados,
            @RequestParam(required = false) UUID usuarioListas,
            Authentication autenticacao,
            Model model) {
        boolean administrador = autenticacao.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        model.addAttribute("pagina", "usuarios");
        model.addAttribute("nome", nome);
        model.addAttribute("arquivados", administrador && arquivados);
        model.addAttribute("resultado", administrador
                ? (arquivados ? usuarios.listar(nome, true, pagina) : usuarios.listar(nome, pagina))
                : new UsuarioApi.Pagina(usuarios.listarParaVisibilidadeListas().stream()
                        .filter(usuario -> usuario.nome().toLowerCase(java.util.Locale.ROOT)
                                .contains(nome.toLowerCase(java.util.Locale.ROOT)))
                        .toList(), 1, 0));
        if (usuarioListas != null) {
            model.addAttribute("usuarioListasSelecionado", buscarUsuarioListas(usuarioListas));
            model.addAttribute("idUsuarioLista", usuarioListas);
        }
        return "usuario/inicio";
    }

    @PostMapping("/usuarios/{id}/situacao/{acao}")
    public String situacao(@PathVariable UUID id, @PathVariable String acao,
            @RequestParam(defaultValue = "false") boolean arquivados, RedirectAttributes redirect) {
        if (!java.util.List.of("arquivar", "reativar", "excluir").contains(acao)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ação inválida.");
        }
        try {
            usuarios.alterarSituacao(id, acao);
            redirect.addFlashAttribute("sucesso", "Operação realizada.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", erro.de(e));
        }
        return "redirect:/usuarios" + (arquivados ? "?arquivados=true" : "");
    }

    @PostMapping("/usuarios")
    public String criar(@RequestParam String nome, @RequestParam String senha,
            @RequestParam String perfil, RedirectAttributes redirect) {
        try {
            usuarios.criar(nome, senha, perfil);
            redirect.addFlashAttribute("sucesso", "Usuário cadastrado.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", erro.de(e));
            redirect.addFlashAttribute("novoNome", nome);
            redirect.addFlashAttribute("novoPerfil", perfil);
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/{id}/perfil")
    public String perfil(@PathVariable UUID id, @RequestParam String perfil, RedirectAttributes redirect) {
        try {
            boolean proprio = usuarios.proprio().id().equals(id);
            usuarios.alterarPerfil(id, perfil);
            redirect.addFlashAttribute("sucesso", "Perfil atualizado.");
            if (proprio && !"ADMIN".equals(perfil)) return "redirect:/agendamentos";
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", erro.de(e));
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/minha-conta")
    public String minhaConta(Model model) {
        model.addAttribute("pagina", "minha-conta");
        var usuario = usuarios.proprio();
        model.addAttribute("usuarioConta", usuario);
        if (!model.containsAttribute("nomeConta")) {
            model.addAttribute("nomeConta", usuario.nome());
        }
        return "usuario/minha-conta";
    }

    @PostMapping("/minha-conta")
    public String atualizarConta(@RequestParam String nome, @RequestParam String senhaAtual,
            @RequestParam(required = false) String novaSenha, RedirectAttributes redirect) {
        try {
            usuarios.atualizarProprio(nome, senhaAtual, novaSenha);
            redirect.addFlashAttribute("sucesso", "Conta atualizada.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", erro.de(e));
            redirect.addFlashAttribute("nomeConta", nome);
        }
        return "redirect:/minha-conta";
    }

    @GetMapping("/usuarios/listas-agendamento")
    public String listarUsuariosParaListas(Model model) {
        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios/listas-agendamento/{id}")
    public String configurarListas(@PathVariable UUID id,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest, Model model) {
        if ("true".equalsIgnoreCase(hxRequest)) {
            try {
                model.addAttribute("usuarioListasSelecionado", buscarUsuarioListas(id));
                model.addAttribute("idUsuarioLista", id);
            } catch (RestClientResponseException e) {
                model.addAttribute("erro", erro.de(e));
            }
            model.addAttribute("modalHtmx", true);
            return "usuario/fragmentos/listas-agendamento :: configuracao";
        }
        return "redirect:/usuarios?usuarioListas=" + id;
    }

    @GetMapping("/usuarios/listas-agendamento/fechar")
    @ResponseBody
    public String fecharListas() {
        return "";
    }

    @PostMapping("/usuarios/listas-agendamento/{id}")
    public String salvarListas(
            @PathVariable UUID id,
            @RequestParam boolean restrita,
            @RequestParam(required = false) java.util.List<UUID> idsListas,
            RedirectAttributes redirect) {
        try {
            usuarios.atualizarListasAgendamento(id, restrita, idsListas == null ? java.util.List.of() : idsListas);
            redirect.addFlashAttribute("sucesso", "Listas de agendamento atualizadas.");
            return "redirect:/usuarios";
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", erro.de(e));
        }
        return "redirect:/usuarios?usuarioListas=" + id;
    }

    private UsuarioListas buscarUsuarioListas(UUID id) {
        var usuario = usuarios.listarParaVisibilidadeListas().stream()
                .filter(item -> item.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        return new UsuarioListas(usuario, usuarios.listasAgendamento(id));
    }

    public record UsuarioListas(UsuarioApi.Usuario usuario, UsuarioApi.VisibilidadeListas visibilidade) {}
}
