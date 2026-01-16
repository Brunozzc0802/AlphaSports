package com.alphasports.service;


import com.alphasports.dto.LoginRequest;
import com.alphasports.dto.RegistroRequest;
import com.alphasports.model.Usuario;
import com.alphasports.model.Cargo;
import com.alphasports.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrar(RegistroRequest request) {
        // Verificar se email já existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setTelefone(request.getTelefone());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setCargo(Cargo.CLIENTE);

        return usuarioRepository.save(usuario);
    }

    public Usuario autenticar(LoginRequest request) {
        System.out.println("=== DEBUG LOGIN ===");
        System.out.println("Email recebido: " + request.getEmail());
        System.out.println("Senha recebida: " + request.getSenha());

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    System.out.println("ERRO: Usuário não encontrado no banco");
                    return new RuntimeException("Email ou senha incorretos");
                });

        System.out.println("Usuário encontrado: " + usuario.getNome());
        System.out.println("Hash no banco: " + usuario.getSenha());

        boolean senhaCorreta = passwordEncoder.matches(request.getSenha(), usuario.getSenha());
        System.out.println("Senha corresponde? " + senhaCorreta);

        if (!senhaCorreta) {
            System.out.println("ERRO: Senha não corresponde");
            throw new RuntimeException("Email ou senha incorretos");
        }

        System.out.println("Login bem-sucedido!");
        return usuario;
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
