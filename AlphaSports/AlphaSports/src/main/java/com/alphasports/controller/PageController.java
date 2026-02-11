package com.alphasports.controller;


import com.alphasports.model.Cliente;
import com.alphasports.service.AdminProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.alphasports.model.Usuario;

@Controller
public class PageController {

    @Autowired
    private AdminProdutoService produtoService;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        // Tenta pegar o Admin/Usuário
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        // Tenta pegar o Cliente
        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");

        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("nomeExibicao", usuario.getNome());
        } else if (cliente != null) {
            model.addAttribute("cliente", cliente);
            model.addAttribute("nomeExibicao", cliente.getNome());
        }
        return "index";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");

        if (usuario != null) {
            model.addAttribute("usuario", usuario);
        } else if (cliente != null) {
            model.addAttribute("cliente", cliente);
            model.addAttribute("nomeExibicao", cliente.getNome());
        }
        return "/perfil";
    }

    @GetMapping("/carrinho")
    public String carrinho(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
        } else if (cliente != null) {
            model.addAttribute("cliente", cliente);
            model.addAttribute("nomeExibicao", cliente.getNome());
        }
        return "/carrinho";
    }
    @GetMapping("/produtos")
    public String produtos(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String busca,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
        } else if (cliente != null) {
            model.addAttribute("nomeExibicao", cliente.getNome());
        }

        if (categoria != null && !categoria.isEmpty()) {
            model.addAttribute("produtos", produtoService.buscarPorCategoria(categoria));
        } else if (busca != null && !busca.isEmpty()) {
            model.addAttribute("produtos", produtoService.buscar(busca));
        } else {
            model.addAttribute("produtos", produtoService.listarAtivo());
        }

        return "/produtos";
    }
}