package com.projeto.la_couro.model.entity;

import com.projeto.la_couro.model.entity.enums.TipoMovimento;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "movimentos_estoque")
public class MovimentoEstoque {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoMovimento tipo;

    @Column(nullable = false)
    private int quantidade;

    private String motivo;

    @Column(name = "realizado_por_id")
    private UUID realizadoPorId;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void prePersist() { this.criadoEm = LocalDateTime.now(); }
}
