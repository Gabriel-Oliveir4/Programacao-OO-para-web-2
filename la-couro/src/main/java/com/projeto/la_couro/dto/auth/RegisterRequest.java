package com.projeto.la_couro.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank String nome,
    @Email @NotBlank String email,
    @NotBlank String senha
) {}
