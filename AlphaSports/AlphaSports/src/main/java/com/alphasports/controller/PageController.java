package com.alphasports.controller;

import com.alphasports.model.Cliente;
import com.alphasports.model.Usuario;
import com.alphasports.repository.ClienteRepository;
import com.alphasports.repository.UsuarioRepository;
import com.alphasports.service.AdminProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private AdminProdutoService produtoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String index(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("nomeExibicao", authentication.getName());
        }
        return "index";
    }

    @GetMapping("/perfil")
    public String perfil(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String email = authentication.getName();

        clienteRepository.findByEmail(email).ifPresent(c -> {
            model.addAttribute("nomeExibicao", c.getNome());
            model.addAttribute("cliente", c);
        });

        usuarioRepository.findByEmail(email).ifPresent(u -> {
            model.addAttribute("nomeExibicao", u.getNome());
            model.addAttribute("usuario", u);
        });

        return "perfil";
    }

    @GetMapping("/carrinho")
    public String carrinho(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            clienteRepository.findByEmail(email).ifPresent(c ->
                    model.addAttribute("nomeExibicao", c.getNome())
            );
        }
        return "carrinho";
    }

    @GetMapping("/produtos")
    public String produtos(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String busca,
            Authentication authentication,
            Model model) {

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            clienteRepository.findByEmail(email).ifPresent(c ->
                    model.addAttribute("nomeExibicao", c.getNome())
            );
        }

        if (categoria != null && !categoria.isEmpty()) {
            model.addAttribute("produtos", produtoService.buscarPorCategoria(categoria));
        } else if (busca != null && !busca.isEmpty()) {
            model.addAttribute("produtos", produtoService.buscar(busca));
        } else {
            model.addAttribute("produtos", produtoService.listarAtivo());
        }
        return "produtos";
    }

    @GetMapping("/api/auth/verificar")
    @ResponseBody
    public ResponseEntity<?> verificarAutenticacao(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            var cliente = clienteRepository.findByEmail(email);
            if (cliente.isPresent()) {
                return ResponseEntity.ok(Map.of("nome", cliente.get().getNome()));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}