package com.projeto.la_couro.controller;

import com.projeto.la_couro.dto.produto.*;
import com.projeto.la_couro.model.entity.Produto;
import com.projeto.la_couro.security.AuthUtils;
import com.projeto.la_couro.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) { this.produtoService = produtoService; }

    @GetMapping
    public ResponseEntity<List<Produto>> listar() {
        return ResponseEntity.ok(produtoService.listarAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Produto> criar(@Valid @RequestBody ProdutoCreateRequest dto) {
        UUID userId = AuthUtils.requireCurrentUserId();
        var p = Produto.builder()
            .nome(dto.nome())
            .tamanho(dto.tamanho())
            .cor(dto.cor())
            .preco(dto.preco())
            .quantidadeEstoque(dto.quantidadeEstoque() == null ? 0 : dto.quantidadeEstoque())
            .fotoUrl(dto.fotoUrl())
            .build();
        Produto salvo = produtoService.criar(p, userId);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Produto> atualizar(@PathVariable UUID id, @Valid @RequestBody ProdutoUpdateRequest dto) {
        UUID userId = AuthUtils.requireCurrentUserId();
        var p = Produto.builder()
            .nome(dto.nome())
            .tamanho(dto.tamanho())
            .cor(dto.cor())
            .preco(dto.preco())
            .fotoUrl(dto.fotoUrl())
            .ativo(dto.ativo() == null ? true : dto.ativo())
            .build();
        return ResponseEntity.ok(produtoService.atualizar(id, p, userId));
    }

    @PatchMapping("/{id}/visibilidade")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> alterarVisibilidade(@PathVariable UUID id, @RequestParam boolean ativo) {
        UUID userId = AuthUtils.requireCurrentUserId();
        produtoService.alterarVisibilidade(id, ativo, userId);
        return ResponseEntity.ok().build();
    }
}
