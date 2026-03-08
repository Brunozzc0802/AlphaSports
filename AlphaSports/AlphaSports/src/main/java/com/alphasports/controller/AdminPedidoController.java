package com.alphasports.controller;

import com.alphasports.model.Pedido;
import com.alphasports.model.StatusPedido;
import com.alphasports.service.EmailService;
import com.alphasports.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class AdminPedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/adminPedidos")
    public String listarPedidos(Model model) {
        List<Pedido> pedidos = pedidoService.listarTodos();
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("statusList", StatusPedido.values());
        return "adminPedidos";
    }
    @PostMapping("/api/pedidos/{id}/alterar-status")
    @ResponseBody
    public ResponseEntity<?> alterarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String novoStatusStr = body.get("status");
        StatusPedido novoStatus;
        try {
            novoStatus = StatusPedido.valueOf(novoStatusStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Status inválido: " + novoStatusStr));
        }

        try {
            Pedido pedidoAtualizado = pedidoService.alterarStatus(id, novoStatus);
            try {
                emailService.enviarAtualizacaoStatus(pedidoAtualizado);
            } catch (Exception emailEx) {
                // Falha no e-mail não deve impedir a resposta de sucesso
                System.err.println("[AdminPedidoController] Erro ao enviar e-mail: "
                        + emailEx.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                    "success",    true,
                    "pedidoId",   id,
                    "novoStatus", novoStatus.name(),
                    "descricao",  novoStatus.getDescricao()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/api/pedidos/{id}/status")
    @ResponseBody
    public ResponseEntity<?> consultarStatus(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.buscarPorId(id);
            return ResponseEntity.ok(Map.of(
                    "pedidoId",  id,
                    "status",    pedido.getStatus().name(),
                    "descricao", pedido.getStatus().getDescricao()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}