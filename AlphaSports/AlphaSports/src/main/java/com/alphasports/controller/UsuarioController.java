package com.alphasports.controller;

import com.alphasports.dto.UsuarioPerfilResponse;
import com.alphasports.dto.UsuarioPerfilUpdateRequest;
import com.alphasports.model.Cliente;
import com.alphasports.model.Usuario;
import com.alphasports.model.Cargo;
import com.alphasports.repository.ClienteRepository;
import com.alphasports.repository.UsuarioRepository;
import com.alphasports.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UsuarioController {

    private final UsuarioService service;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService service,
                             ClienteRepository clienteRepository,
                             UsuarioRepository usuarioRepository) {
        this.service = service;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> perfil(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sessão expirada. Faça login novamente.");
        }

        String email = authentication.getName();

        var clienteOpt = clienteRepository.findByEmail(email);
        if (clienteOpt.isPresent()) {
            return ResponseEntity.ok(clienteOpt.get());
        }

        var usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario u = usuarioOpt.get();
            return ResponseEntity.ok(new UsuarioPerfilResponse(u.getNome(), u.getEmail()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sessão expirada. Faça login novamente.");
    }

    @PutMapping("/perfil")
    public ResponseEntity<?> atualizar(@RequestBody UsuarioPerfilUpdateRequest r,
                                       Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não está logado");
        }

        String email = authentication.getName();

        var usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso permitido apenas para administradores");
        }

        Usuario u = usuarioOpt.get();
        if (u.getCargo() != Cargo.ADMINISTRADOR && u.getCargo() != Cargo.GERENTE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso permitido apenas para administradores");
        }

        try {
            service.atualizarPerfil(u.getId(), r);
            return ResponseEntity.ok("Perfil atualizado com sucesso");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar perfil: " + e.getMessage());
        }
    }
}