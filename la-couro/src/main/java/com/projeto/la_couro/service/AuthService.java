package com.projeto.la_couro.service;

import com.projeto.la_couro.model.repo.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UsuarioRepository usuarioRepo, PasswordEncoder encoder, JwtService jwt) {
        this.usuarioRepo = usuarioRepo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public String login(String email, String senha) {
        var user = usuarioRepo.findByEmail(email)
            .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));
        if (!encoder.matches(senha, user.getSenha())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }
        return jwt.generate(user.getId(), user.getEmail(), user.getRole().name());
    }
}
