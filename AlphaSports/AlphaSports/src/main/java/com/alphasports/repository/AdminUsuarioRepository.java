package com.alphasports.repository;

import com.alphasports.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminUsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByAtivoTrueOrderById();
    List<Usuario> findByAtivoFalseOrderById( );

}
