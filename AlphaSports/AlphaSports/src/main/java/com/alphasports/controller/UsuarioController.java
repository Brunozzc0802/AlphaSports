package com.alphasports.controller;

import com.alphasports.dto.UsuarioPerfilResponse;
import com.alphasports.dto.UsuarioPerfilUpdateRequest;
import com.alphasports.model.Usuario;
import com.alphasports.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> perfil(HttpSession session) {
        try {
            Usuario u = (Usuario) session.getAttribute("usuarioLogado");

            if (u == null) {
                return ResponseEntity.status(401).body("Usuário não está logado");
            }

            UsuarioPerfilResponse response = new UsuarioPerfilResponse(
                    u.getNome(),
                    u.getEmail(),
                    u.getTelefone(),
                    "***.***.***-" + u.getCpf().substring(u.getCpf().length() - 2)
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao buscar perfil: " + e.getMessage());
        }
    }

    @PutMapping("/perfil")
    public ResponseEntity<?> atualizar(@RequestBody UsuarioPerfilUpdateRequest r, HttpSession session) {
        try {
            Usuario u = (Usuario) session.getAttribute("usuarioLogado");

            if (u == null) {
                return ResponseEntity.status(401).body("Usuário não está logado");
            }

            Usuario usuarioAtualizado = service.atualizarPerfil(u.getId(), r);

            // Atualizar o usuário na sessão com os dados novos
            session.setAttribute("usuarioLogado", usuarioAtualizado);

            return ResponseEntity.ok("Perfil atualizado com sucesso");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao atualizar perfil: " + e.getMessage());
        }
    }
}