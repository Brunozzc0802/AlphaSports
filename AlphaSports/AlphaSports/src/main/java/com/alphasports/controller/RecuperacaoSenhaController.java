package com.alphasports.controller;

import com.alphasports.service.RecuperacaoSenhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RecuperacaoSenhaController {

    @Autowired
    private RecuperacaoSenhaService recuperacaoSenhaService;

    @GetMapping("/esqueceu-senha")
    public String exibirFormEmail() {
        return "esqueceu-senha";
    }

    @PostMapping("/esqueceu-senha")
    public String enviarCodigo(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            recuperacaoSenhaService.enviarCodigo(email);
            return "redirect:/verificar-codigo?email=" + email;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
            return "redirect:/esqueceu-senha";
        }
    }

    @GetMapping("/verificar-codigo")
    public String exibirFormCodigo(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "verificar-codigo";
    }

    @PostMapping("/verificar-codigo")
    public String verificarCodigo(@RequestParam String email,
                                  @RequestParam String codigo,
                                  RedirectAttributes redirectAttributes) {
        try {
            recuperacaoSenhaService.validarCodigo(email, codigo);
            return "redirect:/nova-senha?email=" + email + "&codigo=" + codigo;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
            return "redirect:/verificar-codigo?email=" + email;
        }
    }

    @GetMapping("/nova-senha")
    public String exibirFormNovaSenha(@RequestParam String email,
                                      @RequestParam String codigo,
                                      Model model) {
        model.addAttribute("email", email);
        model.addAttribute("codigo", codigo);
        return "nova-senha";
    }

    @PostMapping("/nova-senha")
    public String salvarNovaSenha(@RequestParam String email,
                                  @RequestParam String codigo,
                                  @RequestParam String senha,
                                  @RequestParam String confirmarSenha,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (!senha.equals(confirmarSenha)) {
                throw new RuntimeException("As senhas não coincidem.");
            }
            recuperacaoSenhaService.redefinirSenha(email, codigo, senha);
            return "redirect:/auth/login?sucessoRedefinicao=true";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
            return "redirect:/nova-senha?email=" + email + "&codigo=" + codigo;
        }
    }
}
