package br.com.imbazzu.frontSpring.autenticacao.infraestrutura.seguranca;

import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import br.com.imbazzu.frontSpring.autenticacao.core.exception.RefreshTokenInvalidoException;
import br.com.imbazzu.frontSpring.autenticacao.core.exception.TokenInvalidadoException;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.SessionAuthPorta;
import br.com.imbazzu.frontSpring.usuario.UsuarioApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SessionTokenAuthFilter extends OncePerRequestFilter {
    private final SessionAuthPorta sessionAuth;
    private final UsuarioApi usuarios;

    public SessionTokenAuthFilter(SessionAuthPorta sessionAuth, UsuarioApi usuarios) {
        this.sessionAuth = sessionAuth;
        this.usuarios = usuarios;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/login") || path.equals("/error") || path.equals("/logout")
                || path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (sessionAuth.access() != null) {
            try {
                var usuario = usuarios.proprio();
                if (usuario == null || usuario.nome() == null) {
                    throw new ServletException("A API não retornou os dados do usuário autenticado.");
                }
                var authorities = permissoes(usuario.perfil()).stream().map(SimpleGrantedAuthority::new).toList();
                var auth = UsernamePasswordAuthenticationToken.authenticated(usuario.nome(), null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (TokenInvalidadoException | RefreshTokenInvalidoException e) {
                sessionAuth.clear();
                SecurityContextHolder.clearContext();
            }
        } else {
            SecurityContextHolder.clearContext();
        }
        chain.doFilter(request, response);
    }

    private static List<String> permissoes(String perfil) throws ServletException {
        return switch (perfil) {
            case "AGENDAMENTO" -> List.of("ROLE_AGENDAMENTO");
            case "MOTORISTA" -> List.of("ROLE_AGENDAMENTO", "ROLE_MOTORISTA");
            case "GESTAO" -> List.of("ROLE_AGENDAMENTO", "ROLE_MOTORISTA", "ROLE_GESTAO");
            case "ADMIN" -> List.of("ROLE_AGENDAMENTO", "ROLE_MOTORISTA", "ROLE_GESTAO", "ROLE_ADMIN");
            case null, default -> throw new ServletException("A API retornou um perfil de usuário inválido.");
        };
    }
}
