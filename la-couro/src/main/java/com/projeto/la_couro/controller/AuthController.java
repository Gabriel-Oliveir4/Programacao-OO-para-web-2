package com.projeto.la_couro.controller;

import com.projeto.la_couro.dto.auth.*;
import com.projeto.la_couro.model.entity.Usuario;
import com.projeto.la_couro.service.AuthService;
import com.projeto.la_couro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req.email(), req.senha());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody RegisterRequest req) {
        Usuario u = usuarioService.registrarCliente(req.nome(), req.email(), req.senha());
        return ResponseEntity.ok(u);
    }
}
