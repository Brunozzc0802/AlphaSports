package com.alphasports.controller;

import com.alphasports.model.Usuario;
import com.alphasports.service.AdminUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminUsuarioController {

    @Autowired
    private AdminUsuarioService AdminusuarioService;

    @GetMapping("/adminUsuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> ativos = AdminusuarioService.listarAtivo();
        List<Usuario> Inativos = AdminusuarioService.listarInativo();
        model.addAttribute("ListaAtivos", ativos);
        model.addAttribute("ListaInativos", Inativos);
        return "adminUsuarios";
    }


}
