package com.projeto.la_couro.model.repo;

import com.projeto.la_couro.model.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    List<Pedido> findByUsuarioIdAndVisivel(UUID usuarioId, boolean visivel);
}
