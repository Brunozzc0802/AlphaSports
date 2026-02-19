package com.alphasports.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioPerfilUpdateRequest {

 @NotBlank(message = "Nome é obrigatório")
 @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
 private String nome;

 @NotBlank(message = "Email é obrigatório")
 @Email(message = "Email inválido")
 private String email;

 @NotBlank(message = "Telefone é obrigatório")
 @Size(min = 10, max = 15, message = "Telefone inválido")
 private String telefone;

 @Size(min = 6, max = 50, message = "Senha deve ter entre 6 e 50 caracteres")
 private String senha;

 private String senhaConfirmacao;

}