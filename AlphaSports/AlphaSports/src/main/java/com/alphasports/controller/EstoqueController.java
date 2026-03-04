package com.alphasports.controller;

import com.alphasports.dto.EstoqueDTO;
import com.alphasports.service.EstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/estoque")
@CrossOrigin(origins = "*")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    @PostMapping("/ajustar")
    public ResponseEntity<?> ajustar(@RequestBody EstoqueDTO dto) {
        try {
            estoqueService.ajustarEstoque(
                    dto.getProdutoId(),
                    dto.getQuantidade(),
                    dto.getTipo(),
                    dto.getTamanho()
            );

            return ResponseEntity.ok(Map.of("message", "Estoque atualizado com sucesso"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erro interno no servidor"));
        }
    }
}