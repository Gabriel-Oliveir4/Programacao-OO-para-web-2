package com.projeto.la_couro.model.repo;

import com.projeto.la_couro.model.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, UUID> { }
