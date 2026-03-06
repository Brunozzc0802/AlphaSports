package com.alphasports.controller;

import com.alphasports.model.Produto;
import com.alphasports.repository.ClienteRepository;
import com.alphasports.service.AdminProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProdutoDetalheController {

    @Autowired
    private AdminProdutoService produtoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/produto/{id}")
    public String detalhe(@PathVariable Long id,
                          Authentication authentication,
                          Model model) {

        Produto produto = produtoService.buscarPorId(id);
        model.addAttribute("produto", produto);

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            clienteRepository.findByEmail(email).ifPresent(c ->
                    model.addAttribute("nomeExibicao", c.getNome())
            );
        }

        return "produto-detalhe";
    }
}