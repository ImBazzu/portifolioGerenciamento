package br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig;

import org.springframework.web.client.RestClient;

public class RestClientDesprotegido {

    private final RestClient delegate;

    public RestClientDesprotegido(RestClient delegate){
        this.delegate = delegate;
    }

    public RestClient get(){
        return delegate;
    }
}
