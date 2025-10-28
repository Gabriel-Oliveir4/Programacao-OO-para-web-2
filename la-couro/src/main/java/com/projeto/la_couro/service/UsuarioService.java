package com.projeto.la_couro.service;

/*
 * Serviço responsável por gerenciar criação, listagem e desativação de usuários (ADMIN e CLIENTE)
 */

import com.projeto.la_couro.model.entity.Usuario;
import com.projeto.la_couro.model.entity.enums.Role;
import com.projeto.la_couro.model.repo.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Transactional
    public Usuario registrarCliente(String nome, String email, String senha) {
        repo.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("E-mail já cadastrado");
        });

        var u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha(encoder.encode(senha));
        u.setRole(Role.CLIENTE);
        return repo.save(u);
    }

    @Transactional
    public Usuario registrarAdmin(String nome, String email, String senha, UUID criadorId) {
        if (criadorId == null) {
            throw new IllegalStateException("Autenticação requerida");
        }

        var criador = repo.findById(criadorId)
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado"));
        if (!criador.isAtivo()) {
            throw new IllegalStateException("Conta do criador está desativada");
        }

        if (criador.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Apenas ADMIN pode criar outro ADMIN");
        }

        repo.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("E-mail já cadastrado");
        });

        var u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha(encoder.encode(senha));
        u.setRole(Role.ADMIN);
        return repo.save(u);
    }

    public List<Usuario> listarPorAtivo(boolean ativo) {
        return repo.findByAtivo(ativo);
    }

    public Usuario buscarPorId(UUID id) {
        return repo.findById(id).orElseThrow();
    }

    @Transactional
    public void desativarConta(UUID id) {
        var u = buscarPorId(id);
        if (!u.isAtivo()) return;
        u.setAtivo(false);
        u.setDesativadoEm(LocalDateTime.now());
        repo.save(u);
    }
}
