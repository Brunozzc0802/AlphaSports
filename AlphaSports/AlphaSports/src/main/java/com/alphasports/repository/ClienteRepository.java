package com.alphasports.repository;

import com.alphasports.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    List<Cliente> findByAtivoTrueOrderById();

    List<Cliente> findByAtivoFalseOrderById();
}
