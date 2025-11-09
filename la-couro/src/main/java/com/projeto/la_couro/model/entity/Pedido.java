package com.projeto.la_couro.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.projeto.la_couro.model.entity.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "pedidos")
public class Pedido {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPedido status = StatusPedido.CRIADO;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean visivel = true;

    @Column(name = "pago_em")
    private LocalDateTime pagoEm;

    @Column(name = "pagamento_metodo", length = 20)
    private String pagamentoMetodo;

    @Column(name = "pagamento_referencia", length = 120)
    private String pagamentoReferencia;

    @Column(name = "criado_por_id")
    private UUID criadoPorId;

    @Column(name = "atualizado_por_id")
    private UUID atualizadoPorId;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemPedido> itens;

    @PrePersist
    void prePersist() { this.criadoEm = LocalDateTime.now(); }

    @PreUpdate
    void preUpdate() { this.atualizadoEm = LocalDateTime.now(); }
}
