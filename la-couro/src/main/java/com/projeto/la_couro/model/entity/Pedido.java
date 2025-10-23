// Pedido.java - entidade de pedidos

package com.projeto.la_couro.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID usuarioId;

    @Column(nullable = false, length = 20)
    private String status = "CRIADO";

    @Column(nullable = false)
    private double valorTotal = 0.0;

    @Column(nullable = false)
    private boolean visivel = true;

    private LocalDateTime pagoEm;
    private String pagamentoMetodo;
    private String pagamentoReferencia;

    private UUID criadoPorId;
    private UUID atualizadoPorId;
    private LocalDateTime criadoEm = LocalDateTime.now();
    private LocalDateTime atualizadoEm;

    // Getters e Setters
    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
    public boolean isVisivel() { return visivel; }
    public void setVisivel(boolean visivel) { this.visivel = visivel; }
    public LocalDateTime getPagoEm() { return pagoEm; }
    public void setPagoEm(LocalDateTime pagoEm) { this.pagoEm = pagoEm; }
    public String getPagamentoMetodo() { return pagamentoMetodo; }
    public void setPagamentoMetodo(String pagamentoMetodo) { this.pagamentoMetodo = pagamentoMetodo; }
    public String getPagamentoReferencia() { return pagamentoReferencia; }
    public void setPagamentoReferencia(String pagamentoReferencia) { this.pagamentoReferencia = pagamentoReferencia; }
    public UUID getCriadoPorId() { return criadoPorId; }
    public void setCriadoPorId(UUID criadoPorId) { this.criadoPorId = criadoPorId; }
    public UUID getAtualizadoPorId() { return atualizadoPorId; }
    public void setAtualizadoPorId(UUID atualizadoPorId) { this.atualizadoPorId = atualizadoPorId; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
