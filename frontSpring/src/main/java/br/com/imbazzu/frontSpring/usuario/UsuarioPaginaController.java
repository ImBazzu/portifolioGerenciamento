package br.com.imbazzu.frontSpring.usuario;

import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
            @RequestParam(defaultValue = "false") boolean arquivados, Model model) {
        model.addAttribute("pagina", "usuarios");
        model.addAttribute("nome", nome);
        model.addAttribute("arquivados", arquivados);
        model.addAttribute("resultado", arquivados ? usuarios.listar(nome, true, pagina) : usuarios.listar(nome, pagina));
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
        if (!model.containsAttribute("nomeConta")) {
            model.addAttribute("nomeConta", usuarios.proprio().nome());
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
}
