package com.alphasports.repository;

import com.alphasports.model.Pedido;
import com.alphasports.model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteIdOrderByDataPedidoDesc(Long clienteId);

    List<Pedido> findAllByOrderByDataPedidoDesc();

    List<Pedido> findByStatusOrderByDataPedidoDesc(StatusPedido status);

    @Query("SELECT DISTINCT p FROM Pedido p " +
            "LEFT JOIN FETCH p.itens i " +
            "LEFT JOIN FETCH i.produto prod " +
            "LEFT JOIN FETCH prod.marca " +
            "LEFT JOIN FETCH p.cliente " +
            "ORDER BY p.dataPedido DESC")
    List<Pedido> findAllComItens();
}