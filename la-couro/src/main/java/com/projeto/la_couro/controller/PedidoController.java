package com.projeto.la_couro.controller;

import com.projeto.la_couro.dto.pedido.CriarPedidoRequest;
import com.projeto.la_couro.dto.pedido.PagamentoRequest;
import com.projeto.la_couro.model.entity.Pedido;
import com.projeto.la_couro.security.AuthUtils;
import com.projeto.la_couro.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    public PedidoController(PedidoService pedidoService) { this.pedidoService = pedidoService; }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Pedido>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pedido>> listarPorUsuario(@PathVariable UUID usuarioId,
                                                         @RequestParam(defaultValue = "true") boolean visiveis) {
        UUID requesterId = AuthUtils.requireCurrentUserId();
        if (!AuthUtils.isAdmin() && !requesterId.equals(usuarioId)) {
            throw new AccessDeniedException("Acesso negado");
        }
        return ResponseEntity.ok(pedidoService.listarDoUsuario(usuarioId, visiveis));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscar(@PathVariable UUID id) {
        UUID requesterId = AuthUtils.requireCurrentUserId();
        Pedido pedido = pedidoService.buscar(id);
        if (!AuthUtils.isAdmin() && !pedido.getUsuarioId().equals(requesterId)) {
            throw new AccessDeniedException("Acesso negado");
        }
        return ResponseEntity.ok(pedido);
    }

    @PostMapping
    public ResponseEntity<Pedido> criar(@Valid @RequestBody CriarPedidoRequest req) {
        UUID userId = AuthUtils.requireCurrentUserId();
        if (!AuthUtils.isAdmin() && !userId.equals(req.usuarioId())) {
            throw new AccessDeniedException("Acesso negado");
        }
        var itens = req.itens().stream()
            .map(i -> new com.projeto.la_couro.service.PedidoService.ItemInput(i.produtoId(), i.quantidade()))
            .toList();
        Pedido pedido = pedidoService.criar(req.usuarioId(), itens, userId);
        return ResponseEntity.ok(pedido);
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<Pedido> pagar(@PathVariable UUID id, @Valid @RequestBody PagamentoRequest req) {
        UUID userId = AuthUtils.requireCurrentUserId();
        Pedido p = pedidoService.pagar(id, req.metodo(), req.referencia(), userId);
        return ResponseEntity.ok(p);
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Pedido> cancelar(@PathVariable UUID id) {
        UUID userId = AuthUtils.requireCurrentUserId();
        Pedido cancelado = pedidoService.cancelar(id, userId);
        return ResponseEntity.ok(cancelado);
    }
}
