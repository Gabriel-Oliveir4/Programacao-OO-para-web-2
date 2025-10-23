// PedidoRepository.java - repositório de pedidos

package com.projeto.la_couro.infra.repository;

import com.projeto.la_couro.model.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    List<Pedido> findByUsuarioId(UUID usuarioId);
    List<Pedido> findByUsuarioIdAndVisivelTrue(UUID usuarioId);
    List<Pedido> findByVisivel(boolean visivel);
}
