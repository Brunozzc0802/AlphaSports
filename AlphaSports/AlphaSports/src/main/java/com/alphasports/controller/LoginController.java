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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;


    @GetMapping("/login")
    public String telaPaginaLogin(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "login"; // Busca em src/main/resources/templates/login.html
    }

    @GetMapping("/cadastro")
    public String telaPaginaCadastro(Model model) {
        if (!model.containsAttribute("registroRequest")) {
            model.addAttribute("registroRequest", new RegistroRequest());
        }
        return "cadastro"; // Busca em src/main/resources/templates/cadastro.html
    }


    @PostMapping("/api/auth/registro")
    public String registrar(@Valid @ModelAttribute RegistroRequest request,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            HttpSession session) {

        if (!request.senhasCompatveis()) {
            bindingResult.rejectValue("senhaConfirmacao", "error.senhaConfirmacao",
                    "As senhas não coincidem");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registroRequest", bindingResult);
            redirectAttributes.addFlashAttribute("registroRequest", request);
            return "redirect:/cadastro";
        }

        try {
            Usuario usuario = usuarioService.registrar(request);

            session.setAttribute("usuarioLogado", usuario);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("email", usuario.getEmail());
            session.setAttribute("cargo", usuario.getCargo().toString());

            redirectAttributes.addFlashAttribute("sucesso", "Cadastro realizado com sucesso! Bem-vindo!");
            return "redirect:/";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            redirectAttributes.addFlashAttribute("registroRequest", request);
            return "redirect:/cadastro";
        }
    }


    @PostMapping("/api/auth/login")
    public String login(@Valid @ModelAttribute LoginRequest request,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes,
                        HttpSession session) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginRequest", bindingResult);
            redirectAttributes.addFlashAttribute("loginRequest", request);
            return "redirect:/login";
        }

        try {

            Usuario usuario = usuarioService.autenticar(request);

            session.setAttribute("usuarioLogado", usuario);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("email", usuario.getEmail());
            session.setAttribute("cargo", usuario.getCargo().toString());

            redirectAttributes.addFlashAttribute("sucesso", "Login realizado com sucesso!");
            return "redirect:/";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            redirectAttributes.addFlashAttribute("loginRequest", request);
            return "redirect:/login";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("sucesso", "Logout realizado com sucesso");
        return "redirect:/login";
    }

    @GetMapping("/api/auth/verificar")
    @ResponseBody
    public ResponseEntity<?> verificarSessao(HttpSession session) {
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

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId != null) {
            try {
                Usuario usuario = usuarioService.buscarPorId(usuarioId);
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

    @PostMapping("/api/auth/logout")
    @ResponseBody
    public ResponseEntity<?> logoutAPI(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout realizado com sucesso");
    }

    @GetMapping("/api/auth/verificar-admin")
    @ResponseBody
    public ResponseEntity<?> verificarAdmin(HttpSession session) {
        String cargo = (String) session.getAttribute("cargo");
        if (cargo != null && cargo.equals("ADMINISTRADOR")) {
            return ResponseEntity.ok("Usuário é administrador");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
    }
}