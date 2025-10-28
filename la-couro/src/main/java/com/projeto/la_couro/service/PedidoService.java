package com.projeto.la_couro.service;

import com.projeto.la_couro.model.entity.*;
import com.projeto.la_couro.model.entity.enums.StatusPedido;
import com.projeto.la_couro.model.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepo;
    private final ProdutoRepository produtoRepo;
    private final ItemPedidoRepository itemRepo;
    private final EstoqueService estoqueService;

    public PedidoService(PedidoRepository pedidoRepo, ProdutoRepository produtoRepo,
                         ItemPedidoRepository itemRepo, EstoqueService estoqueService) {
        this.pedidoRepo = pedidoRepo;
        this.produtoRepo = produtoRepo;
        this.itemRepo = itemRepo;
        this.estoqueService = estoqueService;
    }

    public List<Pedido> listarTodos() { return pedidoRepo.findAll(); }

    public List<Pedido> listarDoUsuario(UUID usuarioId, boolean visiveis) {
        return pedidoRepo.findByUsuarioIdAndVisivel(usuarioId, visiveis);
    }

    public Pedido buscar(UUID id) { return pedidoRepo.findById(id).orElseThrow(); }

    @Transactional
    public Pedido criar(UUID usuarioId, List<ItemInput> itens, UUID userId) {
        if (itens == null || itens.isEmpty()) throw new IllegalArgumentException("Sem itens");

        var pedido = new Pedido();
        pedido.setUsuarioId(usuarioId);
        pedido.setCriadoPorId(userId);
        pedido = pedidoRepo.save(pedido);

        BigDecimal total = BigDecimal.ZERO;
        List<ItemPedido> itensEnt = new ArrayList<>();

        for (var it : itens) {
            var produto = produtoRepo.findById(it.produtoId()).orElseThrow();
            var item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(it.quantidade());
            item.setPrecoUnitario(produto.getPreco());
            itensEnt.add(item);
            total = total.add(produto.getPreco().multiply(BigDecimal.valueOf(it.quantidade())));
        }

        itensEnt = itemRepo.saveAll(itensEnt);
        pedido.setItens(itensEnt);
        pedido.setValorTotal(total);
        return pedidoRepo.save(pedido);
    }

    @Transactional
    public Pedido pagar(UUID pedidoId, String metodo, String referencia, UUID userId) {
        var p = buscar(pedidoId);
        if (p.getStatus() != StatusPedido.CRIADO) throw new IllegalStateException("Estado inválido");

        for (var it : p.getItens()) {
            estoqueService.debitar(it.getProduto().getId(), it.getQuantidade(), userId);
        }

        p.setStatus(StatusPedido.PAGO);
        p.setPagamentoMetodo(metodo);
        p.setPagamentoReferencia(referencia);
        p.setPagoEm(LocalDateTime.now());
        p.setAtualizadoPorId(userId);
        return pedidoRepo.save(p);
    }

    @Transactional
    public Pedido cancelar(UUID pedidoId, UUID userId) {
        var p = buscar(pedidoId);
        if (p.getStatus() == StatusPedido.PAGO)
            throw new IllegalStateException("Não cancela pedido pago");

        p.setStatus(StatusPedido.CANCELADO);
        p.setAtualizadoPorId(userId);
        return pedidoRepo.save(p);
    }

    // DTO interno (usado pelos controllers)
    public record ItemInput(UUID produtoId, int quantidade) { }
}
