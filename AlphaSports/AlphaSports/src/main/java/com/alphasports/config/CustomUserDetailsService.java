package com.alphasports.config;

import com.alphasports.model.Usuario;
import com.alphasports.model.Cliente;
import com.alphasports.repository.UsuarioRepository;
import com.alphasports.repository.ClienteRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository,
                                    ClienteRepository clienteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        String emailFormatado = email.toLowerCase().trim();

        var usuarioOpt = usuarioRepository.findByEmail(emailFormatado);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            if (!usuario.getAtivo()) {
                throw new UsernameNotFoundException("Usuário desativado");
            }

            return new CustomUserDetails(
                    usuario.getEmail(),
                    usuario.getSenha(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getCargo().name())),
                    usuario.getNome()
            );
        }

        var clienteOpt = clienteRepository.findByEmail(emailFormatado);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();

            if (!cliente.getAtivo()) {
                throw new UsernameNotFoundException("Cliente desativado");
            }

            return new CustomUserDetails(
                    cliente.getEmail(),
                    cliente.getSenha(),
                    List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")),
                    cliente.getNome()
            );
        }

        throw new UsernameNotFoundException("Usuário não encontrado");
    }

    // 👇 CLASSE INTERNA AQUI MESMO
    private static class CustomUserDetails extends org.springframework.security.core.userdetails.User {

        private final String nome;

        public CustomUserDetails(String username,
                                 String password,
                                 List<SimpleGrantedAuthority> authorities,
                                 String nome) {
            super(username, password, authorities);
            this.nome = nome;
        }

        public String getNome() {
            return nome;
        }
    }
}