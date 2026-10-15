package br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.seguranca;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.excecoes.LoginBloqueadoException;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.LoginTentativasPorta;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginTentativasAdapter implements LoginTentativasPorta {

    private static final int LIMITE = 5;

    private static final int TEMPO_BLOQUEIO_MINUTOS = 15;

    private final Cache<String, AtomicInteger> tentativas = Caffeine.newBuilder()
            .expireAfterWrite(TEMPO_BLOQUEIO_MINUTOS, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    @Override
    public void validarBloqueio(String chave) {
        var contador = tentativas.getIfPresent(chave);
        if (contador != null && contador.get() >= LIMITE) {
            throw new LoginBloqueadoException("Muitas tentativas. Tente novamente em alguns minutos.");
        }
    }

    @Override
    public void registrarFalha(String chave) {
        tentativas.asMap()
                .computeIfAbsent(chave, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    @Override
    public void limparTentativas(String chave) {
        tentativas.invalidate(chave);
    }
}