package com.alphasports.controller;

import com.alphasports.model.Cliente;
import com.alphasports.model.Usuario;
import com.alphasports.service.AdminProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // --- ROTAS DE PÁGINAS (HTML) ---

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");

        if (usuario != null) {
            model.addAttribute("nomeExibicao", usuario.getNome());
        } else if (cliente != null) {
            model.addAttribute("nomeExibicao", cliente.getNome());
        }
        return "index";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (cliente == null && usuario == null) {
            return "redirect:/auth/login";
        }

        if (cliente != null) {
            model.addAttribute("nomeExibicao", cliente.getNome());
        } else {
            model.addAttribute("nomeExibicao", usuario.getNome());
        }

        return "perfil"; // Removido a barra inicial para evitar erros em alguns S.O
    }

    @GetMapping("/carrinho")
    public String carrinho(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");
        if (cliente != null) {
            model.addAttribute("nomeExibicao", cliente.getNome());
        }
        return "carrinho";
    }

    @GetMapping("/produtos")
    public String produtos(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String busca,
            HttpSession session,
            Model model) {

        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");
        if (cliente != null) model.addAttribute("nomeExibicao", cliente.getNome());

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
    public ResponseEntity<?> verificarAutenticacao(HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");
        if (cliente != null) {
            // Retorna um objeto simples com o nome para o header
            return ResponseEntity.ok(Map.of("nome", cliente.getNome()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}