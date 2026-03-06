package com.alphasports.controller;

import com.alphasports.dto.CriarPedidoDTO;
import com.alphasports.model.Pedido;
import com.alphasports.model.StatusPedido;
import com.alphasports.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping("/criar")
    public ResponseEntity<?> criarPedido(@RequestBody CriarPedidoDTO dto,
                                         Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("error", "Não autenticado"));
            }
            Pedido pedido = pedidoService.criarPedido(dto, authentication);
            return ResponseEntity.ok(Map.of(
                    "pedidoId",    pedido.getId(),
                    "codigoPix",   pedido.getCodigoPix(),
                    "total",       pedido.getTotal(),
                    "status",      pedido.getStatus().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/confirmar-pagamento")
    public ResponseEntity<?> confirmarPagamento(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.confirmarPagamento(id);
            return ResponseEntity.ok(Map.of(
                    "pedidoId", pedido.getId(),
                    "status",   pedido.getStatus().name(),
                    "descricao", pedido.getStatus().getDescricao()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> consultarStatus(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.buscarPorId(id);
            return ResponseEntity.ok(Map.of(
                    "pedidoId",  pedido.getId(),
                    "status",    pedido.getStatus().name(),
                    "descricao", pedido.getStatus().getDescricao()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/alterar-status")
    public ResponseEntity<?> alterarStatus(@PathVariable Long id,
                                           @RequestBody Map<String, String> body) {
        try {
            StatusPedido novoStatus = StatusPedido.valueOf(body.get("status"));
            Pedido pedido = pedidoService.alterarStatus(id, novoStatus);
            return ResponseEntity.ok(Map.of(
                    "pedidoId",  pedido.getId(),
                    "status",    pedido.getStatus().name(),
                    "descricao", pedido.getStatus().getDescricao()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status inválido"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}