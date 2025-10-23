// UsuarioService.java - regras de negócio de usuários

package com.projeto.la_couro.service;

import com.projeto.la_couro.model.entity.Usuario;
import com.projeto.la_couro.infra.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrarCliente(String nome, String email, String senha) {
        if (usuarioRepository.existsByEmail(email))
            throw new RuntimeException("E-mail já cadastrado.");

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setRole("CLIENTE");
        return usuarioRepository.save(usuario);
    }

    public Usuario criarAdmin(String nome, String email, String senha) {
        if (usuarioRepository.existsByEmail(email))
            throw new RuntimeException("E-mail já cadastrado.");

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setRole("ADMIN");
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarAtivos(boolean ativo) {
        return usuarioRepository.findByAtivo(ativo);
    }

    public Usuario desativarConta(UUID id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        u.setAtivo(false);
        u.setDesativadoEm(java.time.LocalDateTime.now());
        return usuarioRepository.save(u);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}
