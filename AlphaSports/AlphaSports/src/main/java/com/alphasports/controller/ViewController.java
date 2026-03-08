package com.alphasports.controller;

import com.alphasports.model.Cliente;
import com.alphasports.model.Pedido;
import com.alphasports.repository.ClienteRepository;
import com.alphasports.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ViewController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

   @GetMapping("/meuspedidos")
    public String listarMeusPedidos(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Cliente cliente = clienteRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        List<Pedido> pedidos = pedidoRepository.findByClienteIdOrderByDataPedidoDesc(cliente.getId());

        model.addAttribute("pedidos", pedidos);
        return "meuspedidos";
    }
}