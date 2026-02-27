package com.alphasports.controller;

import com.alphasports.model.Cliente;
import com.alphasports.model.Usuario;
import com.alphasports.service.AdminClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/adminClientes")
public class AdminClienteController {

    @Autowired
    private AdminClienteService clienteService;

    @GetMapping
    public String listarClientes(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario != null) {
            model.addAttribute("nomeExibicao", usuario.getNome());
        }
        model.addAttribute("ListaAtivos", clienteService.listarAtivo());
        model.addAttribute("ListaInativos", clienteService.listarInativo());
        return "adminClientes";
    }

    @PostMapping("/salvar")
    public String salvarCliente(@ModelAttribute Cliente cliente, RedirectAttributes attributes) {
        try {
            clienteService.salvar(cliente);
            attributes.addFlashAttribute("mensagemSucesso",
                    cliente.getId() != null ? "Cliente atualizado com sucesso!" : "Cliente adicionado com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Erro ao salvar: " + e.getMessage());
        }
        return "redirect:/adminClientes";
    }

    @GetMapping("/desativar/{id}")
    public String desativarCliente(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            clienteService.desativar(id);
            attributes.addFlashAttribute("mensagemSucesso", "Cliente desativado com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Erro ao desativar: " + e.getMessage());
        }
        return "redirect:/adminClientes";
    }

    @GetMapping("/ativar/{id}")
    public String ativarCliente(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            clienteService.ativar(id);
            attributes.addFlashAttribute("mensagemSucesso", "Cliente restaurado com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Erro ao restaurar: " + e.getMessage());
        }
        return "redirect:/adminClientes";
    }

    @GetMapping("/dados/{id}")
    @ResponseBody
    public Cliente obterDadosCliente(@PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }
}