package com.alphasports.controller;

import com.alphasports.model.Marca;
import com.alphasports.service.AdminMarcaService;
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
    public String listarMarcas(Model model) {
        List<Marca> ativos = AdminMarcaService.listarAtivo();
        List<Marca> Inativos = AdminMarcaService.listarInativo();
        model.addAttribute("ListaAtivos", ativos);
        model.addAttribute("ListaInativos", Inativos);
        return "adminMarca";
    }
}