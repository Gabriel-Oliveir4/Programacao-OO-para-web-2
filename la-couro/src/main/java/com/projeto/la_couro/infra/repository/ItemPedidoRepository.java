// ItemPedidoRepository.java - repositório de itens de pedido

package com.projeto.la_couro.infra.repository;

import com.projeto.la_couro.model.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, UUID> {
    List<ItemPedido> findByPedidoId(UUID pedidoId);
    void deleteByPedidoId(UUID pedidoId);
}
