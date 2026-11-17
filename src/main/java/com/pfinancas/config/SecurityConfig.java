
package com.pfinancas.config;

import com.pfinancas.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        return http

                // API REST sem sessao
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // Tratamento de erros de seguranca
                .exceptionHandling(exception -> exception

                        // Usuario nao autenticado
                        .authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.sendError(
                                                HttpServletResponse.SC_UNAUTHORIZED,
                                                "Autenticacao necessaria."
                                        )
                        )

                        // Usuario autenticado sem permissao
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) ->
                                        response.sendError(
                                                HttpServletResponse.SC_FORBIDDEN,
                                                "Acesso negado."
                                        )
                        )
                )

                // Regras de acesso
                .authorizeHttpRequests(auth -> auth

                        // Cadastro e login publicos
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/cadastro",
                                "/api/v1/auth/login"
                        ).permitAll()

                        // Documentacao Swagger publica
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Rotas financeiras exigem ROLE_USER
                        .requestMatchers(
                                "/api/v1/transacoes/**",
                                "/api/v1/categorias/**"
                        ).hasRole("USER")

                        // Demais rotas exigem autenticacao
                        .anyRequest().authenticated()
                )

                // Filtro de autenticacao JWT
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}
