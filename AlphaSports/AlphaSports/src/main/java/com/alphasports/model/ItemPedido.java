package com.alphasports.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(name = "nome_produto")
    private String nomeProduto;

    @Column(name = "tamanho")
    private String tamanho;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "preco_unitario", precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "imagem_produto")
    private String imagemProduto;

    public ItemPedido() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public String getImagemProduto() { return imagemProduto; }
    public void setImagemProduto(String imagemProduto) { this.imagemProduto = imagemProduto; }
}