package br.com.imbazzu.frontspring.autenticacao.infraestrutura.httpConfig;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import br.com.imbazzu.frontspring.autenticacao.core.porta.out.SessionAuthPorta;

/**
 * ApiAuthenticationFilter
 */

public class ApiAuthenticationInterceptor implements ClientHttpRequestInterceptor {

    private final SessionAuthPorta sessionAuth;

    public ApiAuthenticationInterceptor(SessionAuthPorta sessionAuth) {
        this.sessionAuth = sessionAuth;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        String token = sessionAuth.access();
        if (token != null) {
            request.getHeaders().setBearerAuth(token);
        }

        return execution.execute(request, body);
    }

}
