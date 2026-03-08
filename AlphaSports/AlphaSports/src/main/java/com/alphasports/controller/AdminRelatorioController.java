package com.alphasports.controller;

import com.alphasports.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminRelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/adminRelatorios")
    public String relatorios(Model model) {
        model.addAttribute("relatorio", relatorioService.gerarRelatorio());
        return "adminRelatorios";
    }
}