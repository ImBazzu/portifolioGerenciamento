package br.com.imbazzu.secretariaTransporte.seguranca.usuario.infrastrutura.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.seguranca.BcriptVerificadorSenhaAdapter;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.seguranca.CriptografarSenhaAdapter;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.seguranca.SecurityFiltroToken;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.application.UsuarioApplicationPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.application.UsuarioCadastroService;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.CriptografiaSenhaPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.TokenPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.UsuarioRepositorioPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.VerificadorSenhaPorta;

@Configuration
@EnableWebSecurity
public class SecurityConfiguracao {

    @Bean
    public SecurityFiltroToken securityFiltroToken(TokenPorta tokenPorta) {
        return new SecurityFiltroToken(tokenPorta);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // --- adapters de infraestrutura ---

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UsuarioApplicationPorta cadastrarUsuarioUseCase(
            UsuarioRepositorioPorta usuarioRepositorioPorta,
            CriptografiaSenhaPorta criptografiaSenhaPorta) {
        return new UsuarioCadastroService(usuarioRepositorioPorta, criptografiaSenhaPorta);
    }

    // --- filtro e filter chain ---

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            SecurityFiltroToken filtroToken) {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(r -> r.anyRequest().permitAll())
                .addFilterBefore(filtroToken, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public VerificadorSenhaPorta verificadorSenhaPorta(PasswordEncoder passwordEncoder) {
        return new BcriptVerificadorSenhaAdapter(passwordEncoder);
    }

    @Bean
    public CriptografiaSenhaPorta criptografiaSenhaPorta(PasswordEncoder passwordEncoder) {
        return new CriptografarSenhaAdapter(passwordEncoder);
    }

}