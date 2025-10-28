package com.projeto.la_couro.controller;

import com.projeto.la_couro.security.AuthUtils;
import com.projeto.la_couro.service.EstoqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @PostMapping("/entrada")
    public ResponseEntity<Void> entrada(@RequestParam UUID produtoId, @RequestParam int qtd) {
        var userId = AuthUtils.getCurrentUserId();
        estoqueService.creditar(produtoId, qtd, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/saida")
    public ResponseEntity<Void> saida(@RequestParam UUID produtoId, @RequestParam int qtd) {
        var userId = AuthUtils.getCurrentUserId();
        estoqueService.debitar(produtoId, qtd, userId);
        return ResponseEntity.ok().build();
    }
}
