// ProdutoService.java - regras de negócio de produtos

package com.projeto.la_couro.service;

import com.projeto.la_couro.model.entity.Produto;
import com.projeto.la_couro.infra.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto criarOuAtualizar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public List<Produto> listarAtivos() {
        return produtoRepository.findByAtivoTrue();
    }

    public Produto alterarVisibilidade(UUID id, boolean ativo) {
        Produto p = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));
        p.setAtivo(ativo);
        return produtoRepository.save(p);
    }

    public Produto buscar(UUID id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));
    }
}
