package com.projeto.la_couro.model.repo;

import com.projeto.la_couro.model.entity.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    @EntityGraph(attributePaths = {"itens", "itens.produto"})
    List<Pedido> findAllBy();

    @EntityGraph(attributePaths = {"itens", "itens.produto"})
    List<Pedido> findByUsuarioIdAndVisivel(UUID usuarioId, boolean visivel);

    @Override
    @EntityGraph(attributePaths = {"itens", "itens.produto"})
    Optional<Pedido> findById(UUID uuid);
}
