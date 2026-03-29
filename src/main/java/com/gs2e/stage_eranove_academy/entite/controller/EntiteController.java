package com.gs2e.stage_eranove_academy.entite.controller;

import com.gs2e.stage_eranove_academy.entite.dto.EntiteDto;
import com.gs2e.stage_eranove_academy.entite.service.EntiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/entites")
@Tag(name = "Entités", description = "API de gestion des entités (CIE, SODECI, GS2E)")
@Slf4j
@RequiredArgsConstructor
public class EntiteController {

    private final EntiteService entiteService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Lister toutes les entités")
    public ResponseEntity<List<EntiteDto>> getAllEntites() {
        log.info("GET /api/entites");
        return ResponseEntity.ok(entiteService.getAllEntites());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer une entité par ID")
    public ResponseEntity<EntiteDto> getEntiteById(@PathVariable Long id) {
        log.info("GET /api/entites/{}", id);
        return ResponseEntity.ok(entiteService.getEntiteById(id));
    }
}
