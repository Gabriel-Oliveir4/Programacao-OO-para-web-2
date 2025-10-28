package com.projeto.la_couro.model.repo;

import com.projeto.la_couro.model.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    List<Produto> findByAtivoTrue();
}
