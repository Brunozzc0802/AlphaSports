package com.alphasports.controller;


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
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
        }
        return "index";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
        }
        return "/perfil";
    }

    @GetMapping("/carrinho")
    public String carrinho(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
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
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
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