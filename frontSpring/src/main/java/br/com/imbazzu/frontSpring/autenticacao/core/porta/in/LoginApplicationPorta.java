package br.com.imbazzu.frontSpring.autenticacao.core.porta.in;

import br.com.imbazzu.frontSpring.autenticacao.core.dto.LoginRequestDto;
import br.com.imbazzu.frontSpring.autenticacao.core.exception.LoginApiException;

public interface LoginApplicationPorta {

    void login(LoginRequestDto loginRequestDto) throws LoginApiException;

    void refresh();
}
