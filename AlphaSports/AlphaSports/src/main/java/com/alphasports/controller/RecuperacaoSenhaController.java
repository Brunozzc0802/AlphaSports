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

    // ─── PASSO 1: Exibe a tela de digitar o e-mail ───────────────────────────
    @GetMapping("/esqueceu-senha")
    public String exibirFormEmail() {
        return "esqueceu-senha";
    }

    // ─── PASSO 1: Recebe o e-mail e envia o código ───────────────────────────
    @PostMapping("/esqueceu-senha")
    public String enviarCodigo(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            recuperacaoSenhaService.enviarCodigo(email);
            // Redireciona para a tela de verificação passando o e-mail pela URL
            return "redirect:/verificar-codigo?email=" + email;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
            return "redirect:/esqueceu-senha";
        }
    }

    // ─── PASSO 2: Exibe a tela de digitar o código ───────────────────────────
    @GetMapping("/verificar-codigo")
    public String exibirFormCodigo(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "verificar-codigo";
    }

    // ─── PASSO 2: Valida o código digitado ───────────────────────────────────
    @PostMapping("/verificar-codigo")
    public String verificarCodigo(@RequestParam String email,
                                  @RequestParam String codigo,
                                  RedirectAttributes redirectAttributes) {
        try {
            recuperacaoSenhaService.validarCodigo(email, codigo);
            // Código válido: redireciona para tela de nova senha
            return "redirect:/nova-senha?email=" + email + "&codigo=" + codigo;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
            return "redirect:/verificar-codigo?email=" + email;
        }
    }

    // ─── PASSO 3: Exibe a tela de digitar nova senha ─────────────────────────
    @GetMapping("/nova-senha")
    public String exibirFormNovaSenha(@RequestParam String email,
                                      @RequestParam String codigo,
                                      Model model) {
        model.addAttribute("email", email);
        model.addAttribute("codigo", codigo);
        return "nova-senha";
    }

    // ─── PASSO 3: Salva a nova senha ─────────────────────────────────────────
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
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Senha redefinida com sucesso! Faça login.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
            return "redirect:/nova-senha?email=" + email + "&codigo=" + codigo;
        }
    }
}