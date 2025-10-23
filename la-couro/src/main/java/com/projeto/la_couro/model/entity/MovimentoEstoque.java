// MovimentoEstoque.java - entidade de movimentações de estoque

package com.projeto.la_couro.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "movimentos_estoque")
public class MovimentoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID produtoId;

    @Column(nullable = false, length = 10)
    private String tipo; // ENTRADA, SAIDA, AJUSTE

    @Column(nullable = false)
    private int quantidade;

    private String motivo;
    private UUID realizadoPorId;
    private LocalDateTime criadoEm = LocalDateTime.now();

    // Getters e Setters
    public UUID getId() { return id; }
    public UUID getProdutoId() { return produtoId; }
    public void setProdutoId(UUID produtoId) { this.produtoId = produtoId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public UUID getRealizadoPorId() { return realizadoPorId; }
    public void setRealizadoPorId(UUID realizadoPorId) { this.realizadoPorId = realizadoPorId; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
