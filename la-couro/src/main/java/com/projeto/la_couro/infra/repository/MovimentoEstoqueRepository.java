// MovimentoEstoqueRepository.java - repositório de movimentações de estoque

package com.projeto.la_couro.infra.repository;

import com.projeto.la_couro.model.entity.MovimentoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface MovimentoEstoqueRepository extends JpaRepository<MovimentoEstoque, UUID> {
    List<MovimentoEstoque> findByProdutoId(UUID produtoId);
}
