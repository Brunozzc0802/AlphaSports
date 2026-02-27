package com.alphasports.service;

import com.alphasports.model.Cliente;
import com.alphasports.repository.ClienteRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class RecuperacaoSenhaService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    // INJEÇÃO DO THYMELEAF AQUI
    @Autowired
    private TemplateEngine templateEngine;

    private final Map<String, CodigoInfo> codigos = new HashMap<>();

    // ─── PASSO 1: Gera e envia o código por e-mail ───────────────────────────
    public void enviarCodigo(String email) {
        // Verifica se o e-mail existe no banco
        clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-mail não encontrado."));

        // Gera código de 6 dígitos
        String codigo = String.format("%06d", new Random().nextInt(999999));

        // Salva o código com validade de 15 minutos
        codigos.put(email, new CodigoInfo(codigo, LocalDateTime.now().plusMinutes(15)));

        // --- NOVO CÓDIGO DE ENVIO DE E-MAIL HTML ---
        try {
            // 1. Prepara a variável do código para o HTML
            Context context = new Context();
            context.setVariable("codigo", codigo);

            // 2. Processa o template HTML (recuperacao-senha.html)
            String htmlProcessado = templateEngine.process("recuperacao-senha", context);

            // 3. Cria a mensagem Mime (suporta HTML)
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("AlphaSports - Código de Recuperação de Senha");
            helper.setText(htmlProcessado, true); // O 'true' avisa que é HTML!

            // 4. Envia o e-mail
            mailSender.send(mensagem);

        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar o e-mail de recuperação: " + e.getMessage());
        }
    }

    // ─── PASSO 2: Valida o código digitado ───────────────────────────────────
    public void validarCodigo(String email, String codigo) {
        CodigoInfo info = codigos.get(email);

        if (info == null) {
            throw new RuntimeException("Nenhum código foi solicitado para este e-mail.");
        }
        if (LocalDateTime.now().isAfter(info.expiracao)) {
            codigos.remove(email);
            throw new RuntimeException("Código expirado. Solicite um novo.");
        }
        if (!info.codigo.equals(codigo)) {
            throw new RuntimeException("Código inválido.");
        }
    }

    // ─── PASSO 3: Redefine a senha ────────────────────────────────────────────
    public void redefinirSenha(String email, String codigo, String novaSenha) {
        validarCodigo(email, codigo);

        if (novaSenha == null || novaSenha.length() < 6) {
            throw new RuntimeException("A senha deve ter no mínimo 6 caracteres.");
        }

        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        cliente.setSenha(passwordEncoder.encode(novaSenha));
        clienteRepository.save(cliente);

        codigos.remove(email);
    }

    private static class CodigoInfo {
        String codigo;
        LocalDateTime expiracao;

        CodigoInfo(String codigo, LocalDateTime expiracao) {
            this.codigo = codigo;
            this.expiracao = expiracao;
        }
    }
}