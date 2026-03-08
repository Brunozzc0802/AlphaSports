package com.alphasports.service;

import com.alphasports.model.Pedido;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void enviarAtualizacaoStatus(Pedido pedido) {
        String destinatario = pedido.getCliente().getEmail();
        String nomeCliente  = pedido.getCliente().getNome();
        String dataPedido = pedido.getDataPedido()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));
        String totalFormatado = String.format("%.2f", pedido.getTotal())
                .replace(".", ",");
        String numeroPedido = String.format("%04d", pedido.getId());

        Context ctx = new Context();
        ctx.setVariable("nomeCliente",    nomeCliente);
        ctx.setVariable("pedidoId",       pedido.getId());
        ctx.setVariable("numeroPedido",   numeroPedido);
        ctx.setVariable("dataPedido",     dataPedido);
        ctx.setVariable("totalFormatado", totalFormatado);
        ctx.setVariable("statusEnum",     pedido.getStatus().name());
        ctx.setVariable("statusDescricao", pedido.getStatus().getDescricao());
        String html = templateEngine.process("atualizacao-pedido", ctx);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("AlphaSports — Pedido #" + numeroPedido
                    + " | " + pedido.getStatus().getDescricao());
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("[EmailService] Falha ao enviar e-mail para "
                    + destinatario + ": " + e.getMessage());
        }
    }
    public void enviarCodigoRecuperacao(String destinatario, String codigo) {
        Context ctx = new Context();
        ctx.setVariable("codigo", codigo);

        String html = templateEngine.process("recuperacao-senha", ctx);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("AlphaSports — Código de Verificação");
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("[EmailService] Falha ao enviar código para "
                    + destinatario + ": " + e.getMessage());
        }
    }
}