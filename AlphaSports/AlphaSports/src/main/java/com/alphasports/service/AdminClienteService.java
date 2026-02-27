package com.alphasports.service;

import com.alphasports.model.Cliente;
import com.alphasports.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Cliente> listarAtivo() {
        return clienteRepository.findByAtivoTrueOrderById();
    }

    public List<Cliente> listarInativo() {
        return clienteRepository.findByAtivoFalseOrderById();
    }

    public Cliente buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID inválido");
        }
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    public Cliente salvar(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new RuntimeException("Nome do cliente é obrigatório");
        }

        // Mesma lógica do AdminUsuarioService:
        // Se tem ID → é edição → busca o existente e trata a senha
        // Se não tem ID → é novo → criptografa direto
        if (cliente.getId() != null) {
            Cliente existente = buscarPorId(cliente.getId());

            // Valida e-mail duplicado apenas para OUTRO cliente
            validarEmailDuplicado(cliente.getEmail(), cliente.getId());

            if (cliente.getSenha() == null || cliente.getSenha().isBlank()) {
                // Senha não preenchida: mantém a do banco
                cliente.setSenha(existente.getSenha());
            } else {
                // Senha nova: criptografa
                cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
            }

            // Preserva o status ativo
            if (cliente.getAtivo() == null) {
                cliente.setAtivo(existente.getAtivo());
            }
        } else {
            // Novo cliente
            validarEmailDuplicado(cliente.getEmail(), null);

            if (cliente.getSenha() == null || cliente.getSenha().isBlank()) {
                throw new RuntimeException("Senha é obrigatória para novos clientes");
            }

            cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
            cliente.setAtivo(true);
        }

        return clienteRepository.save(cliente);
    }

    private void validarEmailDuplicado(String email, Long idAtual) {
        Optional<Cliente> existente = clienteRepository.findByEmail(email);
        if (existente.isPresent()) {
            if (idAtual == null || !existente.get().getId().equals(idAtual)) {
                throw new RuntimeException("E-mail já cadastrado para outro cliente");
            }
        }
    }

    public void desativar(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    public void ativar(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setAtivo(true);
        clienteRepository.save(cliente);
    }
}