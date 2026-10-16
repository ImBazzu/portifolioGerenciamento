package br.com.imbazzu.frontSpring.autenticacao.adapter.out.retry;

import java.util.function.Supplier;

import br.com.imbazzu.frontSpring.autenticacao.core.exception.TokenInvalidadoException;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.in.LoginApplicationPorta;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;

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
