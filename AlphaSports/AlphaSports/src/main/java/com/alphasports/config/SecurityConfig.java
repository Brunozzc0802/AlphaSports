package com.alphasports.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF para facilitar chamadas via JavaScript/Fetch API
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // 1. RECURSOS PÚBLICOS (Arquivos estáticos e telas de login/cadastro)
                        .requestMatchers("/css/**", "/images/**", "/auth/**", "/js/**", "/produto/**").permitAll()
                        .requestMatchers("/esqueceu-senha", "/verificar-codigo", "/nova-senha").permitAll()
                        .requestMatchers("/", "/index", "/home").permitAll()

                        // 2. REGRAS PARA PEDIDOS (APIs)
                        // Usamos '*' no lugar de '**' para o ID, pois o Spring 6 não permite '**' no meio da URL

                        // Admin pode alterar o status
                        .requestMatchers("/api/pedidos/*/alterar-status").hasAnyAuthority("ROLE_ADMINISTRADOR", "ADMINISTRADOR")

                        // Cliente pode criar e confirmar pagamento
                        .requestMatchers("/api/pedidos/criar").hasAnyAuthority("ROLE_CLIENTE", "CLIENTE")
                        .requestMatchers("/api/pedidos/*/confirmar-pagamento").hasAnyAuthority("ROLE_CLIENTE", "CLIENTE")

                        // Consultar status: qualquer um autenticado
                        .requestMatchers("/api/pedidos/*/status").authenticated()

                        // 3. REGRAS DE ADMINISTRAÇÃO
                        .requestMatchers(
                                "/adminUsuarios", "/adminProdutos", "/adminEstoque", "/adminPedidos",
                                "/api/estoque/**", "/desativarUsuario/**", "/ativarUsuario/**"
                        ).hasAnyAuthority("ROLE_ADMINISTRADOR", "ADMINISTRADOR")

                        // 4. REGRAS DE CLIENTE
                        .requestMatchers("/cliente/**", "/perfil", "/perfil/**").hasAnyAuthority("ROLE_CLIENTE", "CLIENTE")

                        // Qualquer outra requisição precisa de login
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            // Redirecionamento inteligente baseado na ROLE
                            var authorities = authentication.getAuthorities();
                            boolean isAdmin = authorities.stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR") || a.getAuthority().equals("ADMINISTRADOR"));

                            if (isAdmin) {
                                response.sendRedirect("/adminUsuarios");
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}