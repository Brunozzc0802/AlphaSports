package com.alphasports.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistroRequest {
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(
            regexp = "\\d{11}",
            message = "CPF deve conter exatamente 11 números"
    )
    private String cpf;
    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(
            regexp = "\\d{10,11}",
            message = "Telefone deve conter 10 ou 11 números"
    )
    private String telefone;
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 100, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotBlank(message = "Confirme sua senha")
    private String senhaConfirmacao;


    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getSenhaConfirmacao() {
        return senhaConfirmacao;
    }

    public void setSenhaConfirmacao(String senhaConfirmacao) {
        this.senhaConfirmacao = senhaConfirmacao;
    }

}
