package br.com.imbazzu.frontspring.autenticacao.core.porta.out;

public interface SessionAuthPorta {
    void store(String accessToken, String refreshToken);

    String access();

    String refresh();

    void clear();
}
