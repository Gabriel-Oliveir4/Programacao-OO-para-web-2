package com.projeto.la_couro.dto.produto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProdutoCreateRequest(
    @NotBlank String nome,
    String tamanho,
    String cor,
    @NotNull @DecimalMin("0.01") BigDecimal preco,
    @Min(0) Integer quantidadeEstoque,
    String fotoUrl
) {}
