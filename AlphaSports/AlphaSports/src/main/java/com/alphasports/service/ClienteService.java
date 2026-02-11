package com.alphasports.service;

import com.alphasports.dto.ClientePerfilResponse;
import com.alphasports.dto.ClientePerfilUpdateRequest;
import com.alphasports.dto.LoginRequest;
import com.alphasports.dto.RegistroRequest;
import com.alphasports.model.Cliente;
import com.alphasports.repository.ClienteRepository;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente autenticar(LoginRequest request) {

        Cliente cliente = clienteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        if (!cliente.getSenha().equals(request.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        return cliente;
    }

    public Cliente cadastrar(RegistroRequest request) {

        if (clienteRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setEmail(request.getEmail());
        cliente.setSenha(request.getSenha()); // depois podemos criptografar

        return clienteRepository.save(cliente);
    }

    public ClientePerfilResponse buscarPerfil(Long id) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return new ClientePerfilResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone()
        );
    }

    public ClientePerfilResponse atualizarPerfil(Long id,
                                                 ClientePerfilUpdateRequest request) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        cliente.setNome(request.getNome());
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());

        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            cliente.setSenha(request.getSenha());
        }

        Cliente atualizado = clienteRepository.save(cliente);

        return new ClientePerfilResponse(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getEmail(),
                atualizado.getTelefone()
        );
    }
}
