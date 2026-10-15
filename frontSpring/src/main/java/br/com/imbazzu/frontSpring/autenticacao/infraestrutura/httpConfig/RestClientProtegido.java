package br.com.imbazzu.frontspring.autenticacao.infraestrutura.httpConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class RestClientProtegido {

    private final RestClient delegate;

    public RestClient get(){
        return delegate;
    }

}
