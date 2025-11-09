package com.projeto.la_couro.dto.auth;

import java.util.UUID;

public record RegisterResponse(UUID id, String nome, String email, String role) {
}
