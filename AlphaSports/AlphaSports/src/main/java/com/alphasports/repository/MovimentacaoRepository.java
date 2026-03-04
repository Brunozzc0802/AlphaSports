package com.alphasports.repository;

import com.alphasports.dto.EstoqueViewDTO;
import com.alphasports.model.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    @Query("""
        SELECT new com.alphasports.dto.EstoqueViewDTO(
            m.produto.id,
            m.produto.nome,
            m.tamanho,
            SUM(CASE WHEN m.tipo = 'ENTRADA' THEN m.quantidade ELSE -m.quantidade END)
        )
        FROM MovimentacaoEstoque m
        GROUP BY m.produto.id, m.produto.nome, m.tamanho
    """)
    List<EstoqueViewDTO> findEstoqueAgrupadoPorProdutoETamanho();
}