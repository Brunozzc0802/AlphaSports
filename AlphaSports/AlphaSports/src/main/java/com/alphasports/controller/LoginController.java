package com.alphasports.controller;

import com.alphasports.dto.LoginRequest;
import com.alphasports.dto.LoginResponse;
import com.alphasports.dto.RegistroRequest;
import com.alphasports.model.Usuario;
import com.alphasports.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

public class LoginController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest request) {
        try {
            Usuario usuario = usuarioService.registrar(request);
            return ResponseEntity.ok(new LoginResponse(
                    "Cadastro realizado com sucesso",
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getCpf(),
                    usuario.getCargo()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        try {
            Usuario usuario = usuarioService.autenticar(request);

            // MUDANÇA AQUI: Salvar o OBJETO Usuario completo na sessão
            session.setAttribute("usuarioLogado", usuario);

            // Manter compatibilidade com código existente
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("email", usuario.getEmail());
            session.setAttribute("cargo", usuario.getCargo().toString());

            return ResponseEntity.ok(new LoginResponse(
                    "Login realizado com sucesso",
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getCpf(),
                    usuario.getCargo()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout realizado com sucesso");
    }

    @GetMapping("/verificar")
    public ResponseEntity<?> verificarSessao(HttpSession session) {
        // Pode usar tanto o objeto completo quanto o ID
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado != null) {
            return ResponseEntity.ok(new LoginResponse(
                    "Sessão ativa",
                    usuarioLogado.getId(),
                    usuarioLogado.getNome(),
                    usuarioLogado.getEmail(),
                    usuarioLogado.getCpf(),
                    usuarioLogado.getCargo()
            ));
        }

        // Fallback para código legado que usa apenas o ID
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId != null) {
            try {
                Usuario usuario = usuarioService.buscarPorId(usuarioId);
                // Atualizar sessão com objeto completo
                session.setAttribute("usuarioLogado", usuario);
                return ResponseEntity.ok(new LoginResponse(
                        "Sessão ativa",
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getCpf(),
                        usuario.getCargo()
                ));
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sessão inválida");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");
    }

    @GetMapping("/verificar-admin")
    public ResponseEntity<?> verificarAdmin(HttpSession session) {
        String cargo = (String) session.getAttribute("cargo");
        if (cargo != null && cargo.equals("ADMINISTRADOR")) {
            return ResponseEntity.ok("Usuário é administrador");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
    }
}