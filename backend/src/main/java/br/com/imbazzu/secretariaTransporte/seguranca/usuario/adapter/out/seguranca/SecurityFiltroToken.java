package br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.seguranca;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.TokenPorta;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class SecurityFiltroToken extends OncePerRequestFilter {

    private final TokenPorta tokenPorta;

    public SecurityFiltroToken(TokenPorta tokenPorta) {
        this.tokenPorta = tokenPorta;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                var dadosToken = tokenPorta.extrairDadosDoTokenAcesso(token);
                var authorities = dadosToken.roles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                var auth = new UsernamePasswordAuthenticationToken(dadosToken.nome(), null,authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}