package com.alphasports.dto;

import com.alphasports.model.Cargo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String mensagem;
    private Long id;
    private String nome;
    private String email;
    private Cargo cargo;
}