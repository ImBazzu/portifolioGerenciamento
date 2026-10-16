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
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, SessionTokenAuthFilter tokenFilter)  {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login","/error","/css/**", "/js/**", "/images/**").permitAll()
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
    public SessionTokenAuthFilter sessionTokenAuthFilter(
            SessionAuthPorta sessionAuth) {
        return new SessionTokenAuthFilter(sessionAuth, new ObjectMapper());
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
