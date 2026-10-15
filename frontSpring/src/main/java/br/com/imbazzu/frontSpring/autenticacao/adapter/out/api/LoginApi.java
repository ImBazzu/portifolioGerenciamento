package br.com.imbazzu.frontspring.autenticacao.adapter.out.api;

import br.com.imbazzu.frontspring.autenticacao.core.exception.LoginApiBloqueadoException;
import br.com.imbazzu.frontspring.autenticacao.core.exception.RefreshTokenInvalidoException;
import br.com.imbazzu.frontspring.autenticacao.infraestrutura.httpConfig.RestClientDesprotegido;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

import br.com.imbazzu.frontspring.autenticacao.core.dto.LoginRefreshDto;
import br.com.imbazzu.frontspring.autenticacao.core.dto.LoginRequestDto;
import br.com.imbazzu.frontspring.autenticacao.core.dto.LoginResponseDto;
import br.com.imbazzu.frontspring.autenticacao.core.porta.out.LoginApiPorta;
import br.com.imbazzu.frontspring.autenticacao.core.exception.LoginApiException;

public class LoginApi implements LoginApiPorta {

    private final RestClient restClient;

    private static final String AUTH_URL = "/auth";

    public LoginApi(RestClientDesprotegido authRest) {
        this.restClient = authRest.get();
    }

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {
        return restClient
                .post()
                .uri(AUTH_URL + "/login")
                .body(dto)
                .retrieve()
                .onStatus(
                        statusCode -> statusCode.isSameCodeAs(HttpStatus.UNAUTHORIZED),
                        (r,q)->{throw new LoginApiException();})
                .onStatus(statusCode->statusCode.isSameCodeAs(HttpStatus.TOO_MANY_REQUESTS),
                        (r,q)->{throw new LoginApiBloqueadoException();})
                .body(LoginResponseDto.class);
    }

    @Override
    public LoginResponseDto refresh(LoginRefreshDto refreshToken) {
        return restClient
                .post()
                .uri(AUTH_URL + "/atualizarToken")
                .body(refreshToken)
                .retrieve()
                .onStatus(
                        statusCode -> statusCode.isSameCodeAs(HttpStatus.UNAUTHORIZED),
                        (r,q)->{throw new RefreshTokenInvalidoException();})
                .body(LoginResponseDto.class);
    }
}
