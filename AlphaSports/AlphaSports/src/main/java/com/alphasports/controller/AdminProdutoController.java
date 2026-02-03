package com.alphasports.controller;

import com.alphasports.model.Produto;
import com.alphasports.service.AdminProdutoService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminProdutoController {

    @Autowired
    private AdminProdutoService produtoService;
    @GetMapping
    public String listarProdutos(Model model) {
        List<Produto> ativos = produtoService.listarAtivo();
        List<Produto> Inativos = produtoService.listarInativo();

        model.addAttribute("ListaAtivos", ativos);
        model.addAttribute("ListaInativos", Inativos);
        return "adminProduto";
    }
    @GetMapping("/novo")
    public String novoProduto(Model model) {
        model.addAttribute("produto", new Produto());
        return "admin/produto-form";
    }
    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute Produto produto) {
        produtoService.salvar(produto);
        return "redirect:/admin";
    }
    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id);
        model.addAttribute("produto", produto);
        return "admin/produto-form";
    }
    @GetMapping("/desativar/{id}")
    public String desativarProduto(@PathVariable Long id, RedirectAttributes attributes) {
        produtoService.desativar(id);
        attributes.addFlashAttribute("mensagemSucesso", "Produto deletado com sucesso!");
        return "redirect:/admin";
    }

    @GetMapping("/ativar/{id}")
    public String ativarProduto(@PathVariable Long id, RedirectAttributes attributes) {
        produtoService.ativar(id);
        attributes.addFlashAttribute("mensagemSucesso", "Produto restaurado com sucesso!");
        return "redirect:/admin";
    }

    @GetMapping("/dados/{id}")
    @ResponseBody
    public Produto obterDadosProduto(@PathVariable Long id) {
        return produtoService.buscarPorId(id);
    }
    @GetMapping("/api/detalhes/{id}")
    @ResponseBody
    public Produto obterDetalhesParaEdicao(@PathVariable Long id) {
        return produtoService.buscarPorId(id);
    }
}
