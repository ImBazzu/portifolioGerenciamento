package br.com.imbazzu.frontspring.autenticacao.adapter.in.controller.Login;



import br.com.imbazzu.frontspring.autenticacao.core.exception.RefreshTokenInvalidoException;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRedirectView;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.imbazzu.frontspring.autenticacao.core.porta.out.SessionAuthPorta;
import br.com.imbazzu.frontspring.autenticacao.core.exception.TokenInvalidadoException;

@ControllerAdvice
public class LoginControllerExcecoes {

    private final SessionAuthPorta sessionAuth;

    public LoginControllerExcecoes(SessionAuthPorta sessionAuth) {
        this.sessionAuth = sessionAuth;
    }

    @ExceptionHandler({TokenInvalidadoException.class,RefreshTokenInvalidoException.class   })
    public Object handleFalhaAutentiacao() {
        sessionAuth.clear(); // Limpa a sessão em caso de exceção
        return new HtmxRedirectView("/login");
    }
}
