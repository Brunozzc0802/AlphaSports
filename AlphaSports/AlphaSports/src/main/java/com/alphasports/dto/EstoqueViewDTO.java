package com.alphasports.dto;

public class EstoqueViewDTO {
    private Long idProduto;
    private String nomeProduto;
    private String tamanho;
    private Long quantidade;

    public EstoqueViewDTO(Long idProduto, String nomeProduto, String tamanho, Long quantidade) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.tamanho = tamanho;
        this.quantidade = quantidade;
    }

    public Long getIdProduto() { return idProduto; }
    public String getNomeProduto() { return nomeProduto; }
    public String getTamanho() { return tamanho; }
    public Long getQuantidade() { return quantidade; }
}