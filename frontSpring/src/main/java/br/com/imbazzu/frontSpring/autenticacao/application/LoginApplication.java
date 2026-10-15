package br.com.imbazzu.frontspring.autenticacao.application;


import br.com.imbazzu.frontspring.autenticacao.core.dto.LoginRefreshDto;
import br.com.imbazzu.frontspring.autenticacao.core.dto.LoginRequestDto;
import br.com.imbazzu.frontspring.autenticacao.core.porta.in.LoginApplicationPorta;
import br.com.imbazzu.frontspring.autenticacao.core.porta.out.LoginApiPorta;
import br.com.imbazzu.frontspring.autenticacao.core.porta.out.SessionAuthPorta;
import br.com.imbazzu.frontspring.autenticacao.core.exception.LoginApiException;
import br.com.imbazzu.frontspring.autenticacao.core.exception.TokenInvalidadoException;

public class LoginApplication implements LoginApplicationPorta {

    private final LoginApiPorta api;

    private final SessionAuthPorta sessionAuth;

    public LoginApplication(LoginApiPorta api, SessionAuthPorta sessionAuth) {
        this.api = api;
        this.sessionAuth = sessionAuth;
    }

    @Override
    public void login(LoginRequestDto loginRequestDto) throws LoginApiException {
        var response = api.login(loginRequestDto);

        sessionAuth.store(response.tokenAcesso(), response.tokenAtualizar());
    }

    @Override
    public void refresh() {
        var tokenAtualizar = sessionAuth.refresh();
        if (tokenAtualizar == null) {
            throw new TokenInvalidadoException();
        }
        var response = api.refresh(new LoginRefreshDto(tokenAtualizar));

        sessionAuth.store(response.tokenAcesso(), response.tokenAtualizar());
    }

}
