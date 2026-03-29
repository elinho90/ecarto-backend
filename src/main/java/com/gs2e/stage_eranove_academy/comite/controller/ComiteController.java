package com.gs2e.stage_eranove_academy.comite.controller;

import com.gs2e.stage_eranove_academy.comite.dto.ComiteDto;
import com.gs2e.stage_eranove_academy.comite.service.ComiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comites")
@Tag(name = "Comités", description = "API de gestion des comités de gouvernance")
@Slf4j
@RequiredArgsConstructor
public class ComiteController {

    private final ComiteService comiteService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Lister tous les comités")
    public ResponseEntity<List<ComiteDto>> getAllComites() {
        log.info("GET /api/comites - Récupération de tous les comités");
        return ResponseEntity.ok(comiteService.getAllComites());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer un comité par ID")
    public ResponseEntity<ComiteDto> getComiteById(@PathVariable Long id) {
        log.info("GET /api/comites/{}", id);
        return ResponseEntity.ok(comiteService.getComiteById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR_SYSTEME')")
    @Operation(summary = "Créer un comité")
    public ResponseEntity<ComiteDto> createComite(@RequestBody ComiteDto dto) {
        log.info("POST /api/comites - Création du comité: {}", dto.getNom());
        ComiteDto saved = comiteService.createComite(dto);
        return ResponseEntity.status(201).body(saved);
    }
}
