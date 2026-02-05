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
@RequestMapping("/admin")
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
        return "admin/produto-form";
    }

    @PostMapping("/salvar")
    public String salvarProduto(
            @RequestParam(required = false) Long id,
            @RequestParam String nome,
            @RequestParam Long marcaId,
            @RequestParam String categoria,
            @RequestParam BigDecimal preco,
            @RequestParam(required = false, defaultValue = "0") Integer desconto,
            @RequestParam(required = false) String descricao,
            @RequestParam String tamanhos,
            @RequestParam(required = false) String imagem,
            @RequestParam(required = false) MultipartFile imagemFile,
            RedirectAttributes redirectAttributes
    ) {
        boolean editando = (id != null);
        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome(nome);
        Marca marca = adminMarcaService.buscarPorId(marcaId);
        produto.setMarca(marca);
        produto.setCategoria(categoria);
        produto.setPreco(preco);
        produto.setDesconto(desconto);
        produto.setDescricao(descricao);
        produto.setTamanhos(tamanhos);
        if (imagemFile != null && !imagemFile.isEmpty()) {
            produto.setImagem(imagemFile.getOriginalFilename());
        } else {
            produto.setImagem(imagem);
        }
        produtoService.salvar(produto);
        if (editando) {
            redirectAttributes.addFlashAttribute(
                    "mensagemSucesso", "Produto atualizado com sucesso!"
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "mensagemSucesso", "Produto cadastrado com sucesso!"
            );
        }
        return "redirect:/admin";
    }


    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id);

        model.addAttribute("produto", produto);
        model.addAttribute("marcas", adminMarcaService.listarAtivo());

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
