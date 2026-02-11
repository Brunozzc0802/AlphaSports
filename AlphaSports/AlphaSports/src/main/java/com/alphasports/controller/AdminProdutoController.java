package com.alphasports.controller;

import com.alphasports.model.Marca;
import com.alphasports.model.Produto;
import com.alphasports.service.AdminMarcaService;
import com.alphasports.service.AdminProdutoService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/produtos")
public class AdminProdutoController {

    @Autowired
    private AdminProdutoService produtoService;
    @Autowired
    private AdminMarcaService adminMarcaService;

    @GetMapping
    public String listarProdutos(Model model) {
        List<Produto> ativos = produtoService.listarAtivo();
        List<Produto> Inativos = produtoService.listarInativo();

        model.addAttribute("ListaAtivos", ativos);
        model.addAttribute("ListaInativos", Inativos);

        model.addAttribute("marcas", adminMarcaService.listarAtivo());

        return "adminProduto";
    }
    @GetMapping("/novo")
    public String novoProduto(Model model) {
        model.addAttribute("produto", new Produto());
        return "adminProduto";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute Produto produto,
                                @RequestParam("marcaId") Long marcaId,
                                @RequestParam(value = "imagemFile", required = false) MultipartFile imagemFile,
                                RedirectAttributes redirectAttributes) {
        try {
            Marca marca = adminMarcaService.buscarPorId(marcaId);
            produto.setMarca(marca);
            if (imagemFile != null && !imagemFile.isEmpty()) {
                produto.setImagem(imagemFile.getOriginalFilename());
            }
            boolean isNovo = (produto.getId() == null);
            produtoService.salvar(produto);
            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    isNovo ? "Produto cadastrado com sucesso!" : "Produto atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar produto: " + e.getMessage());
        }
        return "redirect:/admin/produtos";
    }

    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id);
        model.addAttribute("produto", produto);
        model.addAttribute("marcas", adminMarcaService.listarAtivo());
        return "adminProduto";
    }

    @GetMapping("/desativar/{id}")
    public String desativarProduto(@PathVariable Long id, RedirectAttributes attributes) {
        produtoService.desativar(id);
        attributes.addFlashAttribute("mensagemSucesso", "Produto deletado com sucesso!");
        return "redirect:/admin/produtos";
    }

    @GetMapping("/ativar/{id}")
    public String ativarProduto(@PathVariable Long id, RedirectAttributes attributes) {
        produtoService.ativar(id);
        attributes.addFlashAttribute("mensagemSucesso", "Produto restaurado com sucesso!");
        return "redirect:/admin/produtos";
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
