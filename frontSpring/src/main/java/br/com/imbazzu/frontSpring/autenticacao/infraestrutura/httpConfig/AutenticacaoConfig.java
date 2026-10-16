package br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig;

import br.com.imbazzu.frontSpring.autenticacao.adapter.out.api.LoginApi;
import br.com.imbazzu.frontSpring.autenticacao.adapter.out.sessao.SessionAuth;
import br.com.imbazzu.frontSpring.autenticacao.application.LoginApplication;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.in.LoginApplicationPorta;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.LoginApiPorta;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.SessionAuthPorta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutenticacaoConfig {

    @Bean
    public SessionAuthPorta sessionAuth(HttpServletRequest request) {
        return new SessionAuth(request);
    }

    @Bean
    public ApiAuthenticationInterceptor apiAuthenticationInterceptor(
            SessionAuthPorta sessionAuth) {
        return new ApiAuthenticationInterceptor(sessionAuth);
    }

    @Bean
    public LoginApiPorta loginApi(RestClientDesprotegido restClientDesprotegido) {
        return new LoginApi(restClientDesprotegido);
    }

    @Bean
    public LoginApplicationPorta loginApplication(
            LoginApiPorta loginApi,
            SessionAuthPorta sessionAuth) {
        return new LoginApplication(loginApi, sessionAuth);
    }
}
