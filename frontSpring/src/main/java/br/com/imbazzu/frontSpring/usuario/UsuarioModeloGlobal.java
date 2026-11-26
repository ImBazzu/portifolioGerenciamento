package br.com.imbazzu.frontSpring.usuario;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UsuarioModeloGlobal {
    @ModelAttribute
    public void usuario(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("nomeUsuarioAtual", auth == null ? "" : auth.getName());
        model.addAttribute("podeGerenciarMotoristas", possui(auth, "ROLE_MOTORISTA"));
        model.addAttribute("podeGerenciarOperacao", possui(auth, "ROLE_GESTAO"));
        model.addAttribute("podeGerenciarUsuarios", possui(auth, "ROLE_ADMIN"));
    }

    private boolean possui(Authentication auth, String permissao) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> permissao.equals(a.getAuthority()));
    }
}
