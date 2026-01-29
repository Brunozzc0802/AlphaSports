package com.alphasports.controller;

import com.alphasports.model.Produto;
import org.springframework.ui.Model;
import com.alphasports.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // LISTA ADMIN
    @GetMapping
    public String listarProdutos(Model model) {
        model.addAttribute("produtos", produtoService.listarTodos());
        return "admin/produtos";
    }

    // FORM ADICIONAR
    @GetMapping("/novo")
    public String novoProduto(Model model) {
        model.addAttribute("produto", new Produto());
        return "admin/produto-form";
    }

    // SALVAR
    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute Produto produto) {
        produtoService.salvar(produto);
        return "redirect:/admin/produtos";
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id);
        model.addAttribute("produto", produto);
        return "admin/produto-form";
    }

    // DESATIVAR
    @GetMapping("/desativar/{id}")
    public String desativarProduto(@PathVariable Long id) {
        produtoService.desativar(id);
        return "redirect:/admin/produtos";
    }
}
