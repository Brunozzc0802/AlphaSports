package com.alphasports.service;

import com.alphasports.dto.ClientePerfilResponse;
import com.alphasports.dto.ClientePerfilUpdateRequest;
import com.alphasports.dto.LoginRequest;
import com.alphasports.dto.RegistroRequest;
import com.alphasports.model.Cliente;
import com.alphasports.repository.ClienteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository,
                          PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Cliente autenticar(LoginRequest request) {

        Cliente cliente = clienteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        if (!passwordEncoder.matches(request.getSenha(), cliente.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        return cliente;
    }

    public Cliente cadastrar(RegistroRequest request) {

        if (clienteRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (!request.getSenha().equals(request.getSenhaConfirmacao())) {
            throw new RuntimeException("As senhas não coincidem");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());
        cliente.setSenha(passwordEncoder.encode(request.getSenha()));

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
            cliente.setSenha(passwordEncoder.encode(request.getSenha()));
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
