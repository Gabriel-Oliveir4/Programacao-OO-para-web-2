package com.projeto.la_couro.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "itens_pedido", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"pedido_id", "produto_id"})
})
public class ItemPedido {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private int quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void prePersist() { this.criadoEm = LocalDateTime.now(); }
}
