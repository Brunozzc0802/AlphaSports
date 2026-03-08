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
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/images/**", "/auth/**", "/js/**", "/produto/**").permitAll()
                        .requestMatchers("/esqueceu-senha", "/verificar-codigo", "/nova-senha").permitAll()
                        .requestMatchers("/", "/index", "/home").permitAll()
                        .requestMatchers("/api/pedidos/*/alterar-status").hasAnyAuthority("ROLE_ADMINISTRADOR", "ADMINISTRADOR")
                        .requestMatchers("/api/pedidos/criar").hasAnyAuthority("ROLE_CLIENTE", "CLIENTE")
                        .requestMatchers("/api/pedidos/*/confirmar-pagamento").hasAnyAuthority("ROLE_CLIENTE", "CLIENTE")
                        .requestMatchers("/api/pedidos/*/status").authenticated()
                        .requestMatchers(
                                "/adminUsuarios", "/adminProdutos", "/adminEstoque", "/adminPedidos",
                                "/api/estoque/**", "/desativarUsuario/**", "/ativarUsuario/**"
                        ).hasAnyAuthority("ROLE_ADMINISTRADOR", "ADMINISTRADOR")
                        .requestMatchers("/cliente/**", "/perfil", "/perfil/**","/meuspedidos").hasAnyAuthority("ROLE_CLIENTE", "CLIENTE")
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
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