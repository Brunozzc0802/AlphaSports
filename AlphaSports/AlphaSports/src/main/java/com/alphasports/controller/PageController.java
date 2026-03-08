package com.alphasports.controller;

import com.alphasports.model.Cliente;
import com.alphasports.model.Pedido;
import com.alphasports.model.Usuario;
import com.alphasports.repository.ClienteRepository;
import com.alphasports.repository.UsuarioRepository;
import com.alphasports.service.AdminProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
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
            @RequestParam(required = false) java.math.BigDecimal precoMin,
            @RequestParam(required = false) java.math.BigDecimal precoMax,
            @RequestParam(defaultValue = "relevance") String ordenar,
            Authentication authentication,
            Model model) {

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            clienteRepository.findByEmail(email).ifPresent(c ->
                    model.addAttribute("nomeExibicao", c.getNome())
            );
        }

        java.util.List<com.alphasports.model.Produto> produtos;

        if (categoria != null && !categoria.isEmpty() && !categoria.equals("todos")) {
            produtos = produtoService.buscarPorCategoria(categoria);
        } else if (busca != null && !busca.isEmpty()) {
            produtos = produtoService.buscar(busca);
        } else {
            produtos = produtoService.listarAtivo();
        }

        // Filtro de preço
        if (precoMin != null)
            produtos = produtos.stream().filter(p -> p.getPreco().compareTo(precoMin) >= 0).toList();
        if (precoMax != null)
            produtos = produtos.stream().filter(p -> p.getPreco().compareTo(precoMax) <= 0).toList();

        // Ordenação
        switch (ordenar) {
            case "price-asc"  -> produtos = new java.util.ArrayList<>(produtos);
            case "price-desc" -> produtos = new java.util.ArrayList<>(produtos);
            default -> {}
        }
        if ("price-asc".equals(ordenar))
            produtos.sort(java.util.Comparator.comparing(com.alphasports.model.Produto::getPreco));
        else if ("price-desc".equals(ordenar))
            produtos.sort(java.util.Comparator.comparing(com.alphasports.model.Produto::getPreco).reversed());

        model.addAttribute("produtos", produtos);
        model.addAttribute("totalProdutos", produtos.size());
        model.addAttribute("categoriaAtiva", categoria);

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