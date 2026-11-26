package br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.seguranca;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model.Usuario;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.TokenPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto.DadosTokenDto;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.excecoes.UsuarioNaoAutorizadoException;

public class JwtTokenAdapter implements TokenPorta {

    private final String secret;
    private final String assinatura;

    public JwtTokenAdapter(String secret, String assinatura) {
        this.secret = secret;
        this.assinatura = assinatura;
    }

    @Override
    public String gerarTokenAcesso(Usuario usuario) {
        return JWT.create()
                .withIssuer(assinatura)
                .withSubject(usuario.getNome().valor())
                .withClaim("uid", usuario.getId().toString())
                .withClaim("ver", usuario.getVersaoToken())
                .withClaim("roles", usuario.getPermissoes())
                .withClaim("type", "access")
                .withExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .sign(Algorithm.HMAC256(secret));
    }

    @Override
    public String gerarTokenAtualizacao(Usuario usuario) {
        return JWT.create()
                .withIssuer(assinatura)
                .withSubject(usuario.getNome().valor())
                .withClaim("uid", usuario.getId().toString())
                .withClaim("ver", usuario.getVersaoToken())
                .withClaim("type", "refresh")
                .withExpiresAt(Instant.now().plus(8, ChronoUnit.HOURS))
                .sign(Algorithm.HMAC256(secret));
    }

    @Override
    public DadosTokenDto extrairDadosDoTokenAtualizacao(String token) {
        return extrairDados(verificar(token, "refresh"));
    }

    // JwtTokenAdapter
    @Override
    public DadosTokenDto extrairDadosDoTokenAcesso(String token) {
        return extrairDados(verificar(token, "access"));
    }

    private DecodedJWT verificar(String token, String tipo) {
        try {
            return JWT.require(Algorithm.HMAC256(secret)).withIssuer(assinatura)
                    .withClaim("type", tipo).build().verify(token);
        } catch (JWTVerificationException e) {
            throw new UsuarioNaoAutorizadoException("Token inválido ou expirado.");
        }
    }

    private DadosTokenDto extrairDados(DecodedJWT token) {
        String id = token.getClaim("uid").asString();
        Long versao = token.getClaim("ver").asLong();
        if (id == null || versao == null) throw new UsuarioNaoAutorizadoException("Sessão antiga; entre novamente.");
        try {
            return new DadosTokenDto(UUID.fromString(id), versao);
        } catch (IllegalArgumentException e) {
            throw new UsuarioNaoAutorizadoException("Token inválido.");
        }
    }
}