package br.com.imbazzu.frontspring.autenticacao.core.porta.in;

import br.com.imbazzu.frontspring.autenticacao.core.dto.LoginRequestDto;
import br.com.imbazzu.frontspring.autenticacao.core.exception.LoginApiException;

public interface LoginApplicationPorta {

    void login(LoginRequestDto loginRequestDto) throws LoginApiException;

    void refresh();
}
