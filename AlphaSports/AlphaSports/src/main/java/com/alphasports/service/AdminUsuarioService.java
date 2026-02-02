package com.alphasports.service;

import com.alphasports.model.Usuario;
import com.alphasports.repository.AdminUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUsuarioService {

    @Autowired
    private AdminUsuarioRepository usuarioRepository;

    public List<Usuario> listarAtivo() {
        return usuarioRepository.findByAtivoTrueOrderById();
    }
    public List<Usuario> listarInativo()
    {
        return usuarioRepository.findByAtivoFalseOrderById();
    }

}
