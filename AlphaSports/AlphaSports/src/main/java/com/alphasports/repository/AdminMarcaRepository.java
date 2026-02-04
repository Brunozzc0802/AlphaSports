package com.alphasports.repository;

import com.alphasports.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdminMarcaRepository  extends JpaRepository<Marca, Long> {
        List<Marca> findByAtivoTrueOrderById();
        List<Marca> findByAtivoFalseOrderById( );
}
