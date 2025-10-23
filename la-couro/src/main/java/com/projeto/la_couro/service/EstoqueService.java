// EstoqueService.java - controle de estoque e movimentações

package com.projeto.la_couro.service;

import com.projeto.la_couro.model.entity.*;
import com.projeto.la_couro.infra.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final MovimentoEstoqueRepository movimentoEstoqueRepository;

    public EstoqueService(ProdutoRepository produtoRepository, MovimentoEstoqueRepository movimentoEstoqueRepository) {
        this.produtoRepository = produtoRepository;
        this.movimentoEstoqueRepository = movimentoEstoqueRepository;
    }

    public void registrarMovimento(UUID produtoId, String tipo, int quantidade, String motivo, UUID usuarioId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        int novaQtd = produto.getQuantidadeEstoque();

        if ("ENTRADA".equalsIgnoreCase(tipo)) novaQtd += quantidade;
        else if ("SAIDA".equalsIgnoreCase(tipo)) novaQtd -= quantidade;
        else if ("AJUSTE".equalsIgnoreCase(tipo)) novaQtd += quantidade;
        else throw new RuntimeException("Tipo de movimento inválido.");

        if (novaQtd < 0) throw new RuntimeException("Estoque insuficiente.");

        produto.setQuantidadeEstoque(novaQtd);
        produtoRepository.save(produto);

        MovimentoEstoque mov = new MovimentoEstoque();
        mov.setProdutoId(produtoId);
        mov.setTipo(tipo.toUpperCase());
        mov.setQuantidade(quantidade);
        mov.setMotivo(motivo);
        mov.setRealizadoPorId(usuarioId);
        movimentoEstoqueRepository.save(mov);
    }
}
