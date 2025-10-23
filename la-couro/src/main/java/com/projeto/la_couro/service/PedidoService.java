// PedidoService.java - regras de negócio de pedidos

package com.projeto.la_couro.service;

import com.projeto.la_couro.model.entity.*;
import com.projeto.la_couro.infra.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueService estoqueService;

    public PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository,
                         ProdutoRepository produtoRepository, EstoqueService estoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueService = estoqueService;
    }

    public Pedido criarPedido(UUID usuarioId, List<Map<String, Object>> itens, UUID usuarioResponsavel) {
        Pedido pedido = new Pedido();
        pedido.setUsuarioId(usuarioId);
        pedido.setCriadoPorId(usuarioResponsavel);
        pedido = pedidoRepository.save(pedido);

        double total = 0.0;

        for (Map<String, Object> item : itens) {
            UUID produtoId = UUID.fromString(item.get("produtoId").toString());
            int quantidade = (int) item.get("quantidade");

            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado."));

            if (produto.getQuantidadeEstoque() < quantidade)
                throw new RuntimeException("Estoque insuficiente para " + produto.getNome());

            estoqueService.registrarMovimento(produtoId, "SAIDA", quantidade, "Pedido", usuarioResponsavel);

            ItemPedido ip = new ItemPedido();
            ip.setPedidoId(pedido.getId());
            ip.setProdutoId(produtoId);
            ip.setQuantidade(quantidade);
            ip.setPrecoUnitario(produto.getPreco());
            itemPedidoRepository.save(ip);

            total += produto.getPreco() * quantidade;
        }

        pedido.setValorTotal(total);
        pedidoRepository.save(pedido);

        return pedido;
    }

    public Pedido pagarPedido(UUID id, String metodo, String referencia, UUID usuarioId) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado."));

        if (!"CRIADO".equals(pedido.getStatus()))
            throw new RuntimeException("Pedido não pode ser pago neste status.");

        pedido.setStatus("PAGO");
        pedido.setPagoEm(LocalDateTime.now());
        pedido.setPagamentoMetodo(metodo);
        pedido.setPagamentoReferencia(referencia);
        pedido.setAtualizadoPorId(usuarioId);
        return pedidoRepository.save(pedido);
    }

    public Pedido cancelarPedido(UUID id, UUID usuarioId) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado."));

        if (!"CRIADO".equals(pedido.getStatus()))
            throw new RuntimeException("Somente pedidos CRIADOS podem ser cancelados.");

        List<ItemPedido> itens = itemPedidoRepository.findByPedidoId(id);
        for (ItemPedido ip : itens)
            estoqueService.registrarMovimento(ip.getProdutoId(), "ENTRADA", ip.getQuantidade(), "Cancelamento", usuarioId);

        pedido.setStatus("CANCELADO");
        pedido.setAtualizadoPorId(usuarioId);
        return pedidoRepository.save(pedido);
    }

    public Pedido alterarVisibilidade(UUID id, boolean visivel, UUID usuarioId) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado."));
        pedido.setVisivel(visivel);
        pedido.setAtualizadoPorId(usuarioId);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPorUsuario(UUID usuarioId) {
        return pedidoRepository.findByUsuarioIdAndVisivelTrue(usuarioId);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }
}
