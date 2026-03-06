package com.alphasports.controller;

import com.alphasports.model.Pedido;
import com.alphasports.model.StatusPedido;
import com.alphasports.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminPedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/adminPedidos")
    public String listarPedidos(Model model) {
        List<Pedido> pedidos = pedidoService.listarTodos();
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("statusList", StatusPedido.values());
        return "adminPedidos";
    }
}