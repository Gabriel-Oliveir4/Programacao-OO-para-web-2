package com.projeto.la_couro.dto.pedido;

import jakarta.validation.constraints.*;

public record PagamentoRequest(
    @NotBlank String metodo,
    @NotBlank String referencia
) {}
