package com.alphasports.controller;

import com.alphasports.model.Marca;
import com.alphasports.model.Usuario;
import com.alphasports.service.AdminMarcaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class AdminMarcaController {

    @Autowired
    private AdminMarcaService adminMarcaService;

    @GetMapping("/adminMarca")
    public String listarMarcas(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario != null) {
            model.addAttribute("nomeExibicao", usuario.getNome());
        }

        model.addAttribute("ListaAtivos", adminMarcaService.listarAtivo());
        model.addAttribute("ListaInativos", adminMarcaService.listarInativo());
        return "adminMarca";
    }

    @PostMapping("/salvar")
    public String salvarMarca(@RequestParam(value = "id", required = false) Long id,
                              @RequestParam("nome") String nome,
                              @RequestParam("descricao") String descricao,
                              @RequestParam(value = "logo", required = false) String logoUrl,
                              @RequestParam(value = "arquivoImagem", required = false) MultipartFile arquivo,
                              RedirectAttributes redirectAttributes) {
        try {
            Marca marca;
            String mensagem;

            if (id != null && id > 0) {
                marca = adminMarcaService.buscarPorId(id);
                mensagem = "Marca atualizada com sucesso!";
            } else {
                marca = new Marca();
                marca.setAtivo(true);
                mensagem = "Marca adicionada com sucesso!";
            }
            marca.setNome(nome);
            marca.setDescricao(descricao);
            if (arquivo != null && !arquivo.isEmpty()) {
                String nomeArquivo = System.currentTimeMillis() + "_" + arquivo.getOriginalFilename();
                Path caminho = Paths.get("src/main/resources/static/images/" + nomeArquivo);
                Files.write(caminho, arquivo.getBytes());
                marca.setLogo(nomeArquivo);
            } else if (logoUrl != null && !logoUrl.isBlank()) {
                marca.setLogo(logoUrl);
            }
            adminMarcaService.salvar(marca);
            redirectAttributes.addFlashAttribute("mensagemSucesso", mensagem);
            return "redirect:/adminMarca";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar: " + e.getMessage());
            return "redirect:/adminMarca";
        }
    }

    @GetMapping("/admin/api/marcas/{id}")
    @ResponseBody
    public Marca obterMarca(@PathVariable Long id) {
        return adminMarcaService.buscarPorId(id);
    }

    @GetMapping("/desativar/{id}")
    public String desativarMarca(@PathVariable Long id) {
        Marca marca = adminMarcaService.buscarPorId(id);
        marca.setAtivo(false);
        adminMarcaService.salvar(marca);
        return "redirect:/adminMarca";
    }

    @GetMapping("/ativar/{id}")
    public String ativarMarca(@PathVariable Long id) {
        Marca marca = adminMarcaService.buscarPorId(id);
        marca.setAtivo(true);
        adminMarcaService.salvar(marca);
        return "redirect:/adminMarca";
    }
}