package br.com.imbazzu.frontSpring.autenticacao.infraestrutura.seguranca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.SessionAuthPorta;
import br.com.imbazzu.frontSpring.usuario.UsuarioApi;
import org.springframework.http.HttpMethod;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, SessionTokenAuthFilter tokenFilter)  {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login","/error","/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/usuarios/listas-agendamento", "/usuarios/listas-agendamento/**")
                                .hasRole("GESTAO")
                        .requestMatchers(HttpMethod.GET, "/usuarios").hasRole("GESTAO")
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/motoristas/**").hasRole("MOTORISTA")
                        .requestMatchers(HttpMethod.PATCH, "/pessoas/*/validar", "/pessoas/*/retirar-validacao").hasRole("GESTAO")
                        .requestMatchers("/relatorios/**", "/api/relatorios/**", "/prioridades/**",
                                "/localidade/**", "/cidade/**", "/listas/**", "/veiculo/**").hasRole("GESTAO")
                        .anyRequest().authenticated())
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(htmxAwareEntryPoint()));
        return http.build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean
    public SessionTokenAuthFilter sessionTokenAuthFilter(
            SessionAuthPorta sessionAuth, UsuarioApi usuarios) {
        return new SessionTokenAuthFilter(sessionAuth, usuarios);
    }

    @Bean
    public FilterRegistrationBean<SessionTokenAuthFilter> registroFiltroSessao(SessionTokenAuthFilter filtro) {
        var registro = new FilterRegistrationBean<>(filtro);
        registro.setEnabled(false);
        return registro;
    }

    private AuthenticationEntryPoint htmxAwareEntryPoint() {

        return (request, response, ex) -> {
            System.err.println(">>> ENTRY POINT: "
                    + request.getMethod() + " " + request.getRequestURI());
            if ("true".equals(request.getHeader("HX-Request"))) {
                // HTMX não faz page redirect em respostas 302 —
                // HX-Redirect delega o redirect para o navegador
                System.err.println(ex.getMessage());
                response.setHeader("HX-Redirect", "/login");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendRedirect("/login");
            }
        };
    }
}
