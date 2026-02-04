package com.alphasports.service;

import com.alphasports.model.Marca;
import com.alphasports.repository.AdminMarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminMarcaService {

    @Autowired
    private AdminMarcaRepository marcaRepository;

    public List<Marca> listarAtivo() {
        return marcaRepository.findByAtivoTrueOrderById();
    }
    public List<Marca> listarInativo(){ return marcaRepository.findByAtivoFalseOrderById();}

    public Marca buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID inválido");
        }
        return  marcaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marca não encontrado"));
    }
}

