package com.alphasports.service;

import com.alphasports.model.Produto;
import com.alphasports.model.Usuario;
import com.alphasports.repository.AdminUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public Usuario buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID inválido");
        }
        return  usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void desativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public Usuario salvar(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new RuntimeException("Nome do Usuário é obrigatório");
        }
        return usuarioRepository.save(usuario);
    }

}
