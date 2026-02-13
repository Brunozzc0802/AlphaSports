package com.alphasports.controller;

import com.alphasports.model.Marca;
import com.alphasports.model.Usuario;
import com.alphasports.service.AdminMarcaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminMarcaController {

    @Autowired
    private AdminMarcaService AdminMarcaService;

    @GetMapping("/adminMarca")
    public String listarMarcas(Model model, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario != null) {
            model.addAttribute("nomeExibicao", usuario.getNome());
        }

        List<Marca> ativos = AdminMarcaService.listarAtivo();
        List<Marca> inativos = AdminMarcaService.listarInativo();

        model.addAttribute("ListaAtivos", ativos);
        model.addAttribute("ListaInativos", inativos);

        return "adminMarca";
    }
}