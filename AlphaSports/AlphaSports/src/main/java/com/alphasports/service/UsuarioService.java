package com.alphasports.service;

import com.alphasports.dto.LoginRequest;
import com.alphasports.dto.RegistroRequest;
import com.alphasports.dto.UsuarioPerfilUpdateRequest;
import com.alphasports.model.Usuario;
import com.alphasports.model.Cargo;
import com.alphasports.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrar(RegistroRequest request) {
        // Validações de campos vazios
        validarRegistroRequest(request);


        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Este email já está cadastrado");
        }

        if (usuarioRepository.existsByCpf(request.getCpf())) {
            throw new RuntimeException("Este CPF já está cadastrado");
        }

        if (usuarioRepository.existsByTelefone(request.getTelefone())) {
            throw new RuntimeException("Este telefone já está cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail().toLowerCase().trim());
        usuario.setCpf(request.getCpf());
        usuario.setTelefone(request.getTelefone());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setCargo(Cargo.CLIENTE);

        return usuarioRepository.save(usuario);
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

        boolean senhaCorreta = passwordEncoder.matches(request.getSenha(), usuario.getSenha());

        if (!senhaCorreta) {
            throw new RuntimeException("Email ou senha incorretos");
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

    public Usuario buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email é obrigatório");
        }
        return usuarioRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public Usuario atualizarPerfil(Long id, UsuarioPerfilUpdateRequest request) {
        Usuario usuario = buscarPorId(id);


        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new RuntimeException("Nome é obrigatório");
        }

        if (!usuario.getEmail().equals(request.getEmail())) {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Este email já está cadastrado");
            }
        }
        usuario.setNome(request.getNome().trim());
        usuario.setEmail(request.getEmail().toLowerCase().trim());
        usuario.setTelefone(request.getTelefone());

        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        }

        return usuarioRepository.save(usuario);
    }

    private void validarRegistroRequest(RegistroRequest request) {
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new RuntimeException("Nome é obrigatório");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email é obrigatório");
        }
        if (request.getCpf() == null || request.getCpf().isBlank()) {
            throw new RuntimeException("CPF é obrigatório");
        }
        if (request.getTelefone() == null || request.getTelefone().isBlank()) {
            throw new RuntimeException("Telefone é obrigatório");
        }
        if (request.getSenha() == null || request.getSenha().isBlank()) {
            throw new RuntimeException("Senha é obrigatória");
        }
        if (request.getSenha().length() < 6) {
            throw new RuntimeException("Senha deve ter no mínimo 6 caracteres");
        }
    }

    public boolean emailJaExiste(String email) {
        return usuarioRepository.existsByEmail(email.toLowerCase().trim());
    }

    public boolean cpfJaExiste(String cpf) {
        return usuarioRepository.existsByCpf(cpf);
    }

    public boolean telefoneJaExiste(String telefone) {
        return usuarioRepository.existsByTelefone(telefone);
    }
}