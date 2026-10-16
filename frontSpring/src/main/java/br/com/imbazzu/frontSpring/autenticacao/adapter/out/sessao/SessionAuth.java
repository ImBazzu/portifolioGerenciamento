package br.com.imbazzu.frontSpring.autenticacao.adapter.out.sessao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.SessionAuthPorta;

public class SessionAuth implements SessionAuthPorta {

    private static final String ACCESS = "ACCESS_TOKEN";
    private static final String REFRESH = "REFRESH_TOKEN";

    private final HttpServletRequest request;

    public SessionAuth(HttpServletRequest request) {
        this.request = request;
    }

    private HttpSession getSession(boolean create) {
        return request.getSession(create);
    }

    @Override
    public void store(String accessToken, String refreshToken) {
        HttpSession session = getSession(true);

        session.setAttribute(ACCESS, accessToken);
        session.setAttribute(REFRESH, refreshToken);

        System.out.println("Token Armazenado: " + accessToken + "\n" + refreshToken);
    }

    @Override
    public String access() {
        var session = getSession(false);
        return session == null ? null : (String) session.getAttribute(ACCESS);
    }

    @Override
    public String refresh() {
        var session = getSession(false);
        return session == null ? null : (String) session.getAttribute(REFRESH);
    }

    @Override
    public void clear() {
        var session = getSession(false);
        if (session != null)
            session.invalidate();
    }

}
