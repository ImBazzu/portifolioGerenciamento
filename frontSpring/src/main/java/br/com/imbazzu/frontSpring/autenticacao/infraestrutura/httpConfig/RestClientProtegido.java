package br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig;


import org.springframework.web.client.RestClient;

public class RestClientProtegido {

    private final RestClient delegate;

    public RestClientProtegido(RestClient delegate){
        this.delegate = delegate;
    }

    public RestClient get(){
        return delegate;
    }

}
