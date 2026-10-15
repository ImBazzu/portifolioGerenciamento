package br.com.imbazzu.frontspring.autenticacao.adapter.in.controller.Login;

import br.com.imbazzu.frontspring.autenticacao.core.dto.LoginRequestDto;
import br.com.imbazzu.frontspring.autenticacao.core.exception.LoginApiBloqueadoException;
import br.com.imbazzu.frontspring.autenticacao.core.exception.LoginApiException;
import br.com.imbazzu.frontspring.autenticacao.core.porta.in.LoginApplicationPorta;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final LoginApplicationPorta loginApp;

    public LoginController(LoginApplicationPorta loginApp) {
        this.loginApp = loginApp;
    }

    @GetMapping
    public String login() {
        return "login/login";
    }

    @HxRequest
    @HxReswap(value = HxSwapType.OUTER_HTML)
    @PostMapping
    public Object login(
            @Valid @ModelAttribute LoginRequestDto dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "login/loginForm :: loginFormulario";
        }
        try{
            loginApp.login(dto);
            return new HtmxRedirectView("/veiculo");
        }catch (LoginApiBloqueadoException | LoginApiException ex){
            model.addAttribute("usuario",dto.login());
            model.addAttribute("error", ex.getMessage());
            return "login/loginForm :: loginFormulario";
        }

    }
}