package com.alphasports.repository;

import com.alphasports.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByCategoria(String categoria);

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByAtivoTrueOrderById();
    List<Produto> findByAtivoFalseOrderById( );

}