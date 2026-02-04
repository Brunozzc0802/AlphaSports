package com.alphasports.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data // Adicionei para remover os Getters e Setters manuais e limpar o código
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    // ALTERAÇÃO AQUI: Em vez de String, usamos a entidade Marca
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column
    private Integer desconto;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column
    private String imagem;

    @Column
    private String tamanhos;

    @Column(name = "estoque")
    private Integer estoque;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
}