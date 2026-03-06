package com.alphasports.dto;

import java.math.BigDecimal;
import java.util.List;

public class CriarPedidoDTO {

    private List<ItemPedidoDTO> itens;
    private BigDecimal total;

    public List<ItemPedidoDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoDTO> itens) { this.itens = itens; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public static class ItemPedidoDTO {
        private Long produtoId;
        private String nomeProduto;
        private String tamanho;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private String imagemProduto;

        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

        public String getNomeProduto() { return nomeProduto; }
        public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

        public String getTamanho() { return tamanho; }
        public void setTamanho(String tamanho) { this.tamanho = tamanho; }

        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

        public BigDecimal getPrecoUnitario() { return precoUnitario; }
        public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

        public String getImagemProduto() { return imagemProduto; }
        public void setImagemProduto(String imagemProduto) { this.imagemProduto = imagemProduto; }
    }
}