package br.com.imbazzu.frontspring.autenticacao.adapter.out.retry;

import java.util.function.Supplier;

import br.com.imbazzu.frontspring.autenticacao.core.exception.TokenInvalidadoException;

import br.com.imbazzu.frontspring.autenticacao.core.porta.in.LoginApplicationPorta;
import br.com.imbazzu.frontspring.autenticacao.core.porta.out.RetryComRefreshPorta;

public class RetryComRefresh implements RetryComRefreshPorta {

    private final LoginApplicationPorta loginApplication;

    public RetryComRefresh(LoginApplicationPorta loginApplication) {
        this.loginApplication = loginApplication;
    }

    @Override
    public <T> T executar(Supplier<T> chamadaApi) {
        try {
            return chamadaApi.get();
        } catch (TokenInvalidadoException e) {
            loginApplication.refresh();
            return chamadaApi.get();
        }
    }

}
