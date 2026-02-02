package com.alphasports.service;

import com.alphasports.model.Produto;
import com.alphasports.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class AdminService {

    @Autowired
    private AdminRepository AdminRepository;

    public List<Produto> listarAtivo() {
        return AdminRepository.findByAtivoTrueOrderById();
    }

    public List<Produto> listarInativo() {
        return AdminRepository.findByAtivoFalseOrderById();
    }

    public List<Produto> buscarPorCategoria(String categoria) {
        return AdminRepository.findByCategoria(categoria);
    }

    public List<Produto> buscar(String termo) {
        return AdminRepository.findByNomeContainingIgnoreCase(termo);
    }

    public Produto buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID inválido");
        }
        return AdminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public Produto salvar(Produto produto) {
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new RuntimeException("Nome do produto é obrigatório");
        }
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Preço deve ser maior que zero");
        }
        return AdminRepository.save(produto);
    }

    public void desativar(Long id) {
        Produto produto = buscarPorId(id);
        produto.setAtivo(false);
        AdminRepository.save(produto);
    }

    public void ativar(Long id) {
        Produto produto = buscarPorId(id);
        produto.setAtivo(true);
        AdminRepository.save(produto);
    }

    public void deletar(Long id) {
        if (!AdminRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado");
        }
        AdminRepository.deleteById(id);
    }

    public List<Produto> buscarPorMarca(String marca) {
        return AdminRepository.findByMarca(marca);
    }
}