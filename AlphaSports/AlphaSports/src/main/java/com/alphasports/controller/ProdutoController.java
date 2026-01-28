package com.alphasports.controller;

import com.alphasports.model.Produto;
import com.alphasports.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String busca) {

        List<Produto> produtos;

        if (busca != null && !busca.isEmpty()) {
            produtos = produtoService.buscarPorNome(busca);
        } else if (categoria != null && !categoria.isEmpty() && !categoria.equals("todos")) {
            produtos = produtoService.listarPorCategoria(categoria);
        } else {
            produtos = produtoService.listarAtivos();
        }

        return ResponseEntity.ok(produtos);
    }

    // Buscar produto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        if (produto != null) {
            return ResponseEntity.ok(produto);
        }
        return ResponseEntity.notFound().build();
    }

    // Criar novo produto (apenas admin)
    @PostMapping
    public ResponseEntity<Produto> criarProduto(@RequestBody Produto produto) {
        produto.setAtivo(true); // segurança extra
        Produto novoProduto = produtoService.salvar(produto);
        return ResponseEntity.ok(novoProduto);
    }

    // Atualizar produto (apenas admin)
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizarProduto(
            @PathVariable Long id,
            @RequestBody Produto produto) {

        produto.setId(id);
        Produto produtoAtualizado = produtoService.atualizar(produto);

        if (produtoAtualizado != null) {
            return ResponseEntity.ok(produtoAtualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        boolean desativado = produtoService.desativar(id);

        if (desativado) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Upload de imagem do produto
    @PostMapping("/{id}/imagem")
    public ResponseEntity<String> uploadImagem(
            @PathVariable Long id,
            @RequestParam("imagem") MultipartFile file) {

        try {
            String urlImagem = produtoService.salvarImagem(id, file);
            return ResponseEntity.ok(urlImagem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao fazer upload da imagem");
        }
    }
}
