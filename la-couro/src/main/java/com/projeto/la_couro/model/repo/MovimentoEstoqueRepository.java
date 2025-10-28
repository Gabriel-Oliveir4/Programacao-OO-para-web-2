package com.projeto.la_couro.model.repo;

import com.projeto.la_couro.model.entity.MovimentoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MovimentoEstoqueRepository extends JpaRepository<MovimentoEstoque, UUID> { }
