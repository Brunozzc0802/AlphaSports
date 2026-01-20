package com.alphasports.repository;

import com.alphasports.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByCategoria(String categoria);

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByMaisVendidoTrue();

    List<Produto> findByNovoTrue();

    List<Produto> findByMarca(String marca);
}
