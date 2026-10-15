package br.com.imbazzu.frontspring.autenticacao.infraestrutura.httpConfig;

import br.com.imbazzu.frontspring.autenticacao.adapter.out.retry.RetryComRefresh;
import br.com.imbazzu.frontspring.autenticacao.core.exception.TokenInvalidadoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

import br.com.imbazzu.frontspring.autenticacao.core.porta.in.LoginApplicationPorta;
import br.com.imbazzu.frontspring.autenticacao.core.porta.out.RetryComRefreshPorta;

@Configuration
public class ApiClientConfig {

    private final ApiAuthenticationInterceptor autenticacaoInterceptor;

    public ApiClientConfig(ApiAuthenticationInterceptor authenticationFilter) {
        this.autenticacaoInterceptor = authenticationFilter;
    }

    @Bean
    public RestClientDesprotegido authRestClient(@Value("${api.base-url}") String baseUrl){
        return new RestClientDesprotegido(
                RestClient.builder()
                        .baseUrl(baseUrl)
                        .build()
        );
    }

    @Bean
    public RestClientProtegido protectedRestClient(@Value("${api.base-url}") String baseUrl){
        return new RestClientProtegido(
                RestClient.builder()
                        .baseUrl(baseUrl)
                        .requestInterceptor(autenticacaoInterceptor)
                        .defaultStatusHandler(
                                status-> status.isSameCodeAs(HttpStatus.UNAUTHORIZED),
                                (r,q)->{throw new TokenInvalidadoException();}
                        )
                        .build()
        );
    }

    @Bean
    public RetryComRefreshPorta retryComRefresh(LoginApplicationPorta loginApplication) {
        return new RetryComRefresh(loginApplication);
    }
}