package com.alphasports.repository;

import com.alphasports.model.Produto;
import com.alphasports.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByCategoria(String categoria);

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByMarca(String marca);

    List<Produto> findByAtivoTrueOrderById();
    List<Produto> findByAtivoFalseOrderById( );

}