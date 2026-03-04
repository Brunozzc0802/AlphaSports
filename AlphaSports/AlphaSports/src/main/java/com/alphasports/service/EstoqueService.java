package com.alphasports.service;

import com.alphasports.model.MovimentacaoEstoque;
import com.alphasports.model.Produto;
import com.alphasports.repository.AdminProdutoRepository;
import com.alphasports.repository.MovimentacaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstoqueService {

    @Autowired
    private AdminProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Transactional
    public void ajustarEstoque(Long produtoId, Integer qtd, String tipo, String tamanho) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (tipo.equalsIgnoreCase("ENTRADA")) {
            produto.setEstoque(produto.getEstoque() + qtd);
        } else if (tipo.equalsIgnoreCase("SAIDA")) {
            if (produto.getEstoque() < qtd) {
                throw new RuntimeException("Estoque insuficiente");
            }
            produto.setEstoque(produto.getEstoque() - qtd);
        }

        produtoRepository.save(produto);

        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setProduto(produto);
        mov.setQuantidade(qtd);
        mov.setTipo(tipo);
        mov.setTamanho(tamanho);
        movimentacaoRepository.save(mov);
    }
}
