package br.com.imbazzu.frontspring.autenticacao.core.porta.out;

import java.util.function.Supplier;

public interface RetryComRefreshPorta {
    <T> T executar(Supplier<T> chamadaApi);

}
