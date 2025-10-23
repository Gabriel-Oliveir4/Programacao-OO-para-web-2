// AuthService.java - autenticação (login) e atalhos de criação de usuário

package com.projeto.la_couro.service;

import com.projeto.la_couro.infra.repository.UsuarioRepository;
import com.projeto.la_couro.model.entity.Usuario;
import com.projeto.la_couro.model.dto.auth.LoginResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final com.projeto.la_couro.infra.security.JwtService jwtService; // falta implementar na pasta security

    public AuthService(UsuarioRepository usuarioRepository,
                       UsuarioService usuarioService,
                       PasswordEncoder passwordEncoder,
                       com.projeto.la_couro.infra.security.JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(String email, String senha) {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas."));

        if (!u.isAtivo()) throw new RuntimeException("Conta desativada.");

        if (!passwordEncoder.matches(senha, u.getSenha()))
            throw new RuntimeException("Credenciais inválidas.");

        String token = jwtService.gerarToken(u.getId(), u.getRole(), u.getEmail());
        return new LoginResponse(token, "Bearer");
    }

    // atalhos para manter controllers mais limpos

    public Usuario registrarCliente(String nome, String email, String senha) {
        return usuarioService.registrarCliente(nome, email, senha);
    }

    public Usuario criarAdmin(String nome, String email, String senha) {
        return usuarioService.criarAdmin(nome, email, senha);
    }
}
