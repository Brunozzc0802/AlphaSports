package com.alphasports.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Mantém desativado para o fetch funcionar
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/images/**", "/auth/**", "/js/**").permitAll()
                        .requestMatchers("/esqueceu-senha", "/verificar-codigo", "/nova-senha").permitAll()
                        .requestMatchers("/produto/**").permitAll()

                        // ADMIN: Use hasAnyAuthority para evitar problemas com o prefixo ROLE_
                        .requestMatchers(
                                "/adminUsuarios", "/adminProdutos", "/adminEstoque", "/adminPedidos",
                                "/api/estoque/**", "/api/pedidos/*/alterar-status",
                                "/desativarUsuario/**", "/ativarUsuario/**"
                        ).hasAnyAuthority("ROLE_ADMINISTRADOR", "ADMINISTRADOR")

                        // CLIENTE: Onde está dando o erro 403
                        .requestMatchers(
                                "/cliente/**", "/perfil", "/perfil/**",
                                "/api/pedidos/criar",
                                "/api/pedidos/*/confirmar-pagamento"
                        ).hasAnyAuthority("ROLE_CLIENTE", "CLIENTE")

                        // Qualquer um logado pode ver o status
                        .requestMatchers("/api/pedidos/*/status").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            var authorities = authentication.getAuthorities();
                            if (authorities.stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"))) {
                                response.sendRedirect("/adminUsuarios");
                            } else if (authorities.stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                                response.sendRedirect("/");
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}