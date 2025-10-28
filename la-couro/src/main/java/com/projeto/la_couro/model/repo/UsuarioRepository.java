package com.projeto.la_couro.model.repo;

import com.projeto.la_couro.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByAtivo(boolean ativo);
}
