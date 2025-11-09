package com.projeto.la_couro.controller;

/*
 * Controller responsável pelos endpoints administrativos de usuários.
 */

import com.projeto.la_couro.dto.auth.RegisterRequest;
import com.projeto.la_couro.model.entity.Usuario;
import com.projeto.la_couro.security.AuthUtils;
import com.projeto.la_couro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar(@RequestParam(defaultValue = "true") boolean ativo) {
        return ResponseEntity.ok(usuarioService.listarPorAtivo(ativo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping("/registrar-admin")
    public ResponseEntity<Usuario> registrarAdmin(@Valid @RequestBody RegisterRequest req) {
        UUID criadorId = AuthUtils.requireCurrentUserId();
        Usuario u = usuarioService.registrarAdmin(req.nome(), req.email(), req.senha(), criadorId);
        return ResponseEntity.ok(u);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable UUID id) {
        usuarioService.desativarConta(id);
        return ResponseEntity.ok().build();
    }
}
