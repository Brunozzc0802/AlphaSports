package com.alphasports.service;

import com.alphasports.dto.CriarPedidoDTO;
import com.alphasports.model.*;
import com.alphasports.repository.ClienteRepository;
import com.alphasports.repository.PedidoRepository;
import com.alphasports.repository.AdminProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AdminProdutoRepository produtoRepository;

    public Pedido criarPedido(CriarPedidoDTO dto, Authentication authentication) {
        String email = authentication.getName();
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setTotal(dto.getTotal());
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        pedido.setFormaPagamento("PIX");

        String codigoPix = gerarCodigoPixFicticio(dto.getTotal());
        pedido.setCodigoPix(codigoPix);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        for (CriarPedidoDTO.ItemPedidoDTO itemDTO : dto.getItens()) {
            ItemPedido item = new ItemPedido();
            item.setPedido(pedidoSalvo);
            item.setNomeProduto(itemDTO.getNomeProduto());
            item.setTamanho(itemDTO.getTamanho());
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(itemDTO.getPrecoUnitario());
            item.setSubtotal(itemDTO.getPrecoUnitario().multiply(BigDecimal.valueOf(itemDTO.getQuantidade())));
            item.setImagemProduto(itemDTO.getImagemProduto());

            if (itemDTO.getProdutoId() != null) {
                produtoRepository.findById(itemDTO.getProdutoId()).ifPresent(item::setProduto);
            }

            pedidoSalvo.getItens().add(item);
        }

        return pedidoRepository.save(pedidoSalvo);
    }

    public Pedido confirmarPagamento(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        pedido.setStatus(StatusPedido.PAGO);
        return pedidoRepository.save(pedido);
    }

    public Pedido alterarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllComItens();
    }

    public List<Pedido> listarPorCliente(Authentication authentication) {
        String email = authentication.getName();
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        return pedidoRepository.findByClienteIdOrderByDataPedidoDesc(cliente.getId());
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    private String gerarCodigoPixFicticio(BigDecimal valor) {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 25).toUpperCase();
        return "00020126580014br.gov.bcb.pix0136" + uuid +
                "5204000053039865406" + String.format("%.2f", valor).replace(".", "") +
                "5802BR5913ALPHASPORTS6009SAO PAULO62070503***6304ABCD";
    }
}