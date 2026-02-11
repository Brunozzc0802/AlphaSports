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

    @PostMapping("/registro")
    public String registrar(@ModelAttribute RegistroRequest request) {

        clienteService.cadastrar(request);

        return "redirect:/auth/login?sucesso=true";
    }

    @PostMapping("/login")
    public String autenticar(@ModelAttribute LoginRequest request,
                             HttpServletRequest httpRequest) {

        HttpSession oldSession = httpRequest.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession session = httpRequest.getSession(true);
        try {
            Usuario usuario = usuarioService.autenticar(request);
            session.setAttribute("usuarioLogado", usuario);
            session.setMaxInactiveInterval(60 * 30);
            return "redirect:/adminUsuarios";
        } catch (RuntimeException ignored) {
        }
        try {
            Cliente cliente = clienteService.autenticar(request);
            session.setAttribute("clienteLogado", cliente);
            session.setMaxInactiveInterval(60 * 30);
            return "redirect:/";
        } catch (RuntimeException ignored) {
        }
        return "redirect:/auth/login?erro=true";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "redirect:/auth/login?logout=true";
    }
}
