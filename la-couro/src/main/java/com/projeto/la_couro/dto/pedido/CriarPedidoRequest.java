package com.projeto.la_couro.dto.pedido;

import jakarta.validation.constraints.*;
import java.util.*;
import java.util.UUID;

public record CriarPedidoRequest(
    @NotNull UUID usuarioId,
    @NotNull @Size(min = 1) List<ItemInput> itens
) {
    public record ItemInput(@NotNull UUID produtoId, @Min(1) int quantidade) {}
}
