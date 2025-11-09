package com.projeto.la_couro.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projeto.la_couro.model.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "usuarios")
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @Email @NotBlank
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 255)
    @JsonIgnore
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.CLIENTE;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "desativado_em")
    private LocalDateTime desativadoEm;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    void prePersist() { this.criadoEm = LocalDateTime.now(); }

    @PreUpdate
    void preUpdate() { this.atualizadoEm = LocalDateTime.now(); }
}
