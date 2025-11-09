package com.projeto.la_couro.service;

import com.projeto.la_couro.model.entity.Produto;
import com.projeto.la_couro.model.repo.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ProdutoService {
    private final ProdutoRepository repo;

    public ProdutoService(ProdutoRepository repo) { this.repo = repo; }

    public List<Produto> listarAtivos() { return repo.findByAtivoTrue(); }

    public Produto buscarPorId(UUID id) { return repo.findById(id).orElseThrow(); }

    @Transactional
    public Produto criar(Produto p, UUID userId) {
        if (userId == null) {
            throw new IllegalStateException("Autenticação requerida");
        }
        p.setId(null);
        p.setCriadoPorId(userId);
        return repo.save(p);
    }

    @Transactional
    public Produto atualizar(UUID id, Produto input, UUID userId) {
        if (userId == null) {
            throw new IllegalStateException("Autenticação requerida");
        }
        var p = buscarPorId(id);
        p.setNome(input.getNome());
        p.setTamanho(input.getTamanho());
        p.setCor(input.getCor());
        p.setPreco(input.getPreco());
        p.setFotoUrl(input.getFotoUrl());
        if (input.isAtivo() != p.isAtivo()) p.setAtivo(input.isAtivo());
        p.setAtualizadoPorId(userId);
        return repo.save(p);
    }

    @Transactional
    public void alterarVisibilidade(UUID id, boolean ativo, UUID userId) {
        if (userId == null) {
            throw new IllegalStateException("Autenticação requerida");
        }
        var p = buscarPorId(id);
        p.setAtivo(ativo);
        p.setAtualizadoPorId(userId);
        repo.save(p);
    }
}
