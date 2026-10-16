package br.com.imbazzu.frontSpring.autenticacao.infraestrutura.seguranca;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.SessionAuthPorta;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SessionTokenAuthFilter extends OncePerRequestFilter {

    private final SessionAuthPorta sessionAuth;

    private final ObjectMapper objectMapper;

    public SessionTokenAuthFilter(SessionAuthPorta sessionAuth, ObjectMapper objectMapper) {
        this.sessionAuth = sessionAuth;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println(">>> REQUEST: " + request.getMethod() + " " + request.getRequestURI());
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            var accessToken = sessionAuth.access();

            System.out.println(">>> TOKEN NA SESSÃO: " + accessToken); // temporário

            if (accessToken != null) {
                JwtClaims claims = extractClaims(accessToken);
                System.out.println(">>> CLAIMS: " + claims); // temporário

                if (claims != null) {
                    var authorities = claims.roles().stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();
                    var auth = UsernamePasswordAuthenticationToken.authenticated(
                            claims.subject(), null, authorities);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println(">>> AUTENTICAÇÃO SETADA: " + auth.getName()
                            + " authorities=" + auth.getAuthorities()); // adiciona isso

                }
            }
        }
        filterChain.doFilter(request, response);

    }

    private JwtClaims extractClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3)
                return null;

            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode payload = objectMapper.readTree(payloadBytes);

            String subject = payload.path("sub").asText(null);

            if (subject == null) {
                return null; // token malformado — trata como não autenticado
            }

            String type = payload.path("type").asText(null);
            if (!"access".equals(type)) {
                return null; // token malformado — trata como não autenticado
            }

            List<String> roles = new ArrayList<>();
            JsonNode rolesNode = payload.path("roles"); // ajuste o campo conforme seu backend
            if (rolesNode.isArray()) {
                rolesNode.forEach(r -> roles.add(r.asText()));
            }

            return new JwtClaims(subject, roles);

        } catch (Exception e) {
            return null; // token malformado — trata como não autenticado
        }
    }

    private record JwtClaims(String subject, List<String> roles) {

    }

}
