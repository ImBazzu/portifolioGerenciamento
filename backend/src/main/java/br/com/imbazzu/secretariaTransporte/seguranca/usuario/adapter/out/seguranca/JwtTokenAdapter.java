package br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.seguranca;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model.Usuario;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.TokenPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto.DadosTokenDto;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
                .withClaim("type", "refresh")
                .withExpiresAt(Instant.now().plus(8, ChronoUnit.HOURS))
                .sign(Algorithm.HMAC256(secret));
    }

    @Override
    public String extrairDadosDoTokenAtualizacao(String token) {
        var decoded = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(assinatura)
                .withClaim("type", "refresh")
                .build()
                .verify(token);

        return decoded.getSubject();
    }

    // JwtTokenAdapter
    @Override
    public DadosTokenDto extrairDadosDoTokenAcesso(String token) {
        var decoded = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(assinatura)
                .withClaim("type", "access")
                .build()
                .verify(token); // uma única verificação

        return new DadosTokenDto(
                decoded.getSubject(),
                decoded.getClaim("roles").asList(String.class)
        );
    }
}