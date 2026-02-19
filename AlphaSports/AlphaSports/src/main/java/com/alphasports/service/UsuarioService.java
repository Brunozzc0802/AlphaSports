package com.alphasports.service;

import com.alphasports.dto.LoginRequest;
import com.alphasports.dto.UsuarioPerfilUpdateRequest;
import com.alphasports.model.Usuario;
import com.alphasports.model.Cargo;
import com.alphasports.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario autenticar(LoginRequest request) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email é obrigatório");
        }

        if (request.getSenha() == null || request.getSenha().isBlank()) {
            throw new RuntimeException("Senha é obrigatória");
        }

        String emailNormalizado = request.getEmail().toLowerCase().trim();

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new RuntimeException("Email ou senha incorretos"));

        if (!usuario.getAtivo()) {
            throw new RuntimeException("Usuário está desativado");
        }

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Email ou senha incorretos");
        }

        if (usuario.getCargo() != Cargo.ADMINISTRADOR &&
                usuario.getCargo() != Cargo.GERENTE) {

            throw new RuntimeException("Acesso permitido apenas para administradores");
        }

        return usuario;
    }

    public Usuario buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID inválido");
        }

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public Usuario atualizarPerfil(Long id, UsuarioPerfilUpdateRequest request) {

        Usuario usuario = buscarPorId(id);

        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new RuntimeException("Nome é obrigatório");
        }

        if (!usuario.getEmail().equals(request.getEmail())) {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email já está cadastrado");
            }
        }

        usuario.setNome(request.getNome().trim());
        usuario.setEmail(request.getEmail().toLowerCase().trim());

        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        }

        return usuarioRepository.save(usuario);
    }
}
