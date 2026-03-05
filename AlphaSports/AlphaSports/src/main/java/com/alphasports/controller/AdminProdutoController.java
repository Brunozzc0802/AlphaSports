package com.alphasports.controller;

import com.alphasports.model.Marca;
import com.alphasports.model.Produto;
import com.alphasports.model.Usuario;
import com.alphasports.service.AdminMarcaService;
import com.alphasports.service.AdminProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/adminProdutos")
public class AdminProdutoController {

    @Autowired
    private AdminProdutoService produtoService;

    @Autowired
    private AdminMarcaService adminMarcaService;

    // Lê o valor de app.upload.dir do application.properties
    @Value("${app.upload.dir:uploads/imagens}")
    private String uploadDir;

    @GetMapping
    public String listarProdutos(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario != null) {
            model.addAttribute("nomeExibicao", usuario.getNome());
        }

        model.addAttribute("ListaAtivos", produtoService.listarAtivo());
        model.addAttribute("ListaInativos", produtoService.listarInativo());
        model.addAttribute("marcas", adminMarcaService.listarAtivo());

        return "adminProduto";
    }

    @GetMapping("/novo")
    public String novoProduto(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("marcas", adminMarcaService.listarAtivo());
        return "adminProduto";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute Produto produto,
                                @RequestParam("marcaId") Long marcaId,
                                @RequestParam(value = "imagemFile", required = false) MultipartFile imagemFile,
                                RedirectAttributes redirectAttributes) {
        try {
            // Associa a marca
            Marca marca = adminMarcaService.buscarPorId(marcaId);
            produto.setMarca(marca);

            // Faz o upload da imagem se foi enviada
            if (imagemFile != null && !imagemFile.isEmpty()) {
                String nomeArquivo = salvarImagem(imagemFile);
                produto.setImagem(nomeArquivo);
            }
            // Se for edição sem nova imagem, mantém a imagem anterior (não sobrescreve)

            boolean isNovo = (produto.getId() == null);
            produtoService.salvar(produto);

            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    isNovo ? "Produto cadastrado com sucesso!" : "Produto atualizado com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar produto: " + e.getMessage());
        }

        return "redirect:/adminProdutos";
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
        attributes.addFlashAttribute("mensagemSucesso", "Produto desativado com sucesso!");
        return "redirect:/adminProdutos";
    }

    @GetMapping("/ativar/{id}")
    public String ativarProduto(@PathVariable Long id, RedirectAttributes attributes) {
        produtoService.ativar(id);
        attributes.addFlashAttribute("mensagemSucesso", "Produto restaurado com sucesso!");
        return "redirect:/adminProdutos";
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

    // -------------------------------------------------------
    // Método privado: salva o arquivo em disco e retorna o nome
    // -------------------------------------------------------
    private String salvarImagem(MultipartFile arquivo) throws IOException {
        // Cria a pasta se não existir
        Path pasta = Paths.get(uploadDir);
        Files.createDirectories(pasta);

        // Gera nome único para evitar colisões (ex: uuid_tenis.jpg)
        String extensao = "";
        String nomeOriginal = arquivo.getOriginalFilename();
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        }
        String nomeArquivo = UUID.randomUUID().toString() + extensao;

        // Copia o arquivo para a pasta de uploads
        Path destino = pasta.resolve(nomeArquivo);
        Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return nomeArquivo;
    }
}