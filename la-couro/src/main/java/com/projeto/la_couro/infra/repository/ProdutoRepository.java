// ProdutoRepository.java - repositório de produtos

package com.projeto.la_couro.infra.repository;

import com.projeto.la_couro.model.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    List<Produto> findByAtivoTrue();
    List<Produto> findByAtivo(boolean ativo);
}
