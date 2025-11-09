package com.projeto.la_couro.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "produtos")
public class Produto {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    private String tamanho;
    private String cor;

    @Positive
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Builder.Default
    @Column(name = "quantidade_estoque", nullable = false)
    private int quantidadeEstoque = 0;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_por_id")
    private UUID criadoPorId;

    @Column(name = "atualizado_por_id")
    private UUID atualizadoPorId;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    void prePersist() { this.criadoEm = LocalDateTime.now(); }

    @PreUpdate
    void preUpdate() { this.atualizadoEm = LocalDateTime.now(); }
}
