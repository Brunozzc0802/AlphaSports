package com.alphasports.controller;

import com.alphasports.dto.LoginRequest;
import com.alphasports.dto.RegistroRequest;
import com.alphasports.model.Usuario;
import com.alphasports.model.Cliente;
import com.alphasports.service.UsuarioService;
import com.alphasports.service.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class LoginController {

    private final UsuarioService usuarioService;
    private final ClienteService clienteService;

    public LoginController(UsuarioService usuarioService,
                           ClienteService clienteService) {
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
    }

    @GetMapping("/login")
    public String exibirLogin(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @GetMapping("/cadastro")
    public String exibirCadastro(Model model) {
        model.addAttribute("registroRequest", new RegistroRequest());
        return "cadastro";
    }

    @PostMapping("/registrar")
    public String registrar(@ModelAttribute RegistroRequest request,
                            RedirectAttributes attributes) {
        try {
            clienteService.cadastrar(request);
            attributes.addFlashAttribute("sucesso", "Cadastro realizado com sucesso! Faça login.");
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            return "cadastro";
        }
    }
}
