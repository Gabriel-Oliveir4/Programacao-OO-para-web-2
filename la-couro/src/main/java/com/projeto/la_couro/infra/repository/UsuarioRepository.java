// UsuarioRepository.java - repositório de usuários

package com.projeto.la_couro.infra.repository;

import com.projeto.la_couro.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByAtivo(boolean ativo);
    boolean existsByEmail(String email);
}
