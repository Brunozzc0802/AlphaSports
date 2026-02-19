package com.alphasports.controller;

import com.alphasports.dto.UsuarioPerfilResponse;
import com.alphasports.dto.UsuarioPerfilUpdateRequest;
import com.alphasports.model.Cliente;
import com.alphasports.model.Usuario;
import com.alphasports.model.Cargo;
import com.alphasports.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
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

    private boolean usuarioAdminOuGerente(Usuario u) {
        return u.getCargo() == Cargo.ADMINISTRADOR ||
                u.getCargo() == Cargo.GERENTE;
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> perfil(HttpSession session) {
        // Busca primeiro o usuário (Admin/Funcionario)
        Usuario u = (Usuario) session.getAttribute("usuarioLogado");
        if (u != null) {
            return ResponseEntity.ok(new UsuarioPerfilResponse(u.getNome(), u.getEmail()));
        }

        // Se não for admin, busca o Cliente
        Cliente c = (Cliente) session.getAttribute("clienteLogado");
        if (c != null) {
            return ResponseEntity.ok(c); // Retorna os dados do cliente
        }

        // Se nenhum dos dois existir, aí sim retorna 401
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sessão expirada. Faça login novamente.");
    }

    @PutMapping("/perfil")
    public ResponseEntity<?> atualizar(@RequestBody UsuarioPerfilUpdateRequest r,
                                       HttpSession session) {
        try {
            Usuario u = (Usuario) session.getAttribute("usuarioLogado");

            if (u == null) {
                return ResponseEntity.status(401).body("Usuário não está logado");
            }

            if (!usuarioAdminOuGerente(u)) {
                return ResponseEntity.status(403).body("Acesso permitido apenas para administradores");
            }

            Usuario usuarioAtualizado = service.atualizarPerfil(u.getId(), r);

            session.setAttribute("usuarioLogado", usuarioAtualizado);

            return ResponseEntity.ok("Perfil atualizado com sucesso");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao atualizar perfil: " + e.getMessage());
        }
    }
}
