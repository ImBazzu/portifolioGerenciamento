package br.com.imbazzu.secretariaTransporte.seguranca.usuario.infrastrutura.configuracao;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.persistencia.UsuarioJpaRepository;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.persistencia.UsuarioRepositorioAdapter;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.seguranca.JwtTokenAdapter;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.seguranca.LoginTentativasAdapter;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.application.AtualizacaoService;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.application.AutenticacaoService;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.in.AtualizarTokenUseCase;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.in.AutenticarUsuarioUseCase;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.LoginTentativasPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.TokenPorta;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.out.UsuarioRepositorioPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.VerificadorSenhaPorta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsuarioBeansConfiguracao {


    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.assinatura}")
    private String assinatura;

    @Bean
    public TokenPorta tokenPorta() {
        return new JwtTokenAdapter(secret, assinatura);
    }

    @Bean
    public LoginTentativasPorta loginTentativasPorta() {
        return new LoginTentativasAdapter();
    }

    @Bean
    public UsuarioRepositorioPorta usuarioRepositoryPorta(UsuarioJpaRepository jpaRepository) {
        return new UsuarioRepositorioAdapter(jpaRepository);
    }

    @Bean
    public AutenticarUsuarioUseCase autenticacaoService(UsuarioRepositorioPorta usuarioRepositorioPorta,
                                                        TokenPorta tokenPorta, LoginTentativasPorta loginTentativasPorta,
                                                        VerificadorSenhaPorta verificadorSenhaPorta) {
        return new AutenticacaoService(usuarioRepositorioPorta, tokenPorta,
                loginTentativasPorta, verificadorSenhaPorta);
    }


    @Bean
    public AtualizarTokenUseCase atualizacaoService(UsuarioRepositorioPorta usuarioRepositorioPorta, TokenPorta tokenPorta) {
        return new AtualizacaoService(usuarioRepositorioPorta,tokenPorta);
    }

}
