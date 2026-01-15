package com.alphasports.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Cargo cargo;


}