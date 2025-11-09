package com.projeto.la_couro.service;

import com.projeto.la_couro.model.entity.MovimentoEstoque;
import com.projeto.la_couro.model.entity.Produto;
import com.projeto.la_couro.model.entity.enums.TipoMovimento;
import com.projeto.la_couro.model.repo.MovimentoEstoqueRepository;
import com.projeto.la_couro.model.repo.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class EstoqueService {
    private final ProdutoRepository produtoRepo;
    private final MovimentoEstoqueRepository movRepo;

    public EstoqueService(ProdutoRepository produtoRepo, MovimentoEstoqueRepository movRepo) {
        this.produtoRepo = produtoRepo; this.movRepo = movRepo;
    }

    @Transactional
    public void creditar(UUID produtoId, int qtd, UUID userId) {
        if (qtd <= 0) throw new IllegalArgumentException("qtd > 0");
        if (userId == null) throw new IllegalStateException("Usuário responsável obrigatório");
        var p = produtoRepo.findById(produtoId).orElseThrow();
        p.setQuantidadeEstoque(p.getQuantidadeEstoque() + qtd);
        produtoRepo.save(p);
        registrarMov(p, TipoMovimento.ENTRADA, qtd, "Entrada manual", userId);
    }

    @Transactional
    public void debitar(UUID produtoId, int qtd, UUID userId) {
        if (qtd <= 0) throw new IllegalArgumentException("qtd > 0");
        if (userId == null) throw new IllegalStateException("Usuário responsável obrigatório");
        var p = produtoRepo.findById(produtoId).orElseThrow();
        if (p.getQuantidadeEstoque() < qtd) throw new IllegalStateException("Estoque insuficiente");
        p.setQuantidadeEstoque(p.getQuantidadeEstoque() - qtd);
        produtoRepo.save(p);
        registrarMov(p, TipoMovimento.SAIDA, qtd, "Saída manual", userId);
    }

    private void registrarMov(Produto p, TipoMovimento tipo, int qtd, String motivo, UUID userId) {
        var m = new MovimentoEstoque();
        m.setProduto(p);
        m.setTipo(tipo);
        m.setQuantidade(qtd);
        m.setMotivo(motivo);
        m.setRealizadoPorId(userId);
        movRepo.save(m);
    }
}
