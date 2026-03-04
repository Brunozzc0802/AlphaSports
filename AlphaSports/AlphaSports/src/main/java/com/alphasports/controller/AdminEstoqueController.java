package com.alphasports.controller;

import com.alphasports.dto.EstoqueViewDTO;
import com.alphasports.repository.AdminProdutoRepository;
import com.alphasports.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminEstoqueController {

    @Autowired
    private AdminProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @GetMapping("/adminEstoque")
    public String exibirTelaEstoque(Model model) {
        List<EstoqueViewDTO> listaEstoque = movimentacaoRepository.findEstoqueAgrupadoPorProdutoETamanho();
        model.addAttribute("listaEstoque", listaEstoque);
        model.addAttribute("todosProdutos", produtoRepository.findAll());
        return "adminEstoque";
    }
}