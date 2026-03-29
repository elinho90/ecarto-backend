package com.gs2e.stage_eranove_academy.phase.controller;

import com.gs2e.stage_eranove_academy.phase.dto.PhaseDto;
import com.gs2e.stage_eranove_academy.phase.service.PhaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/phases")
@Tag(name = "Phases", description = "API de gestion des phases de projet")
@Slf4j
@RequiredArgsConstructor
public class PhaseController {

    private final PhaseService phaseService;

    @GetMapping("/projet/{projetId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Lister les phases d'un projet")
    public ResponseEntity<List<PhaseDto>> getPhasesByProjet(@PathVariable Long projetId) {
        log.info("GET /api/phases/projet/{}", projetId);
        return ResponseEntity.ok(phaseService.getPhasesByProjet(projetId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer une phase par son ID")
    public ResponseEntity<PhaseDto> getPhaseById(@PathVariable Long id) {
        log.info("GET /api/phases/{}", id);
        return ResponseEntity.ok(phaseService.getPhaseById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ADMINISTRATEUR_SYSTEME')")
    @Operation(summary = "Créer une nouvelle phase")
    public ResponseEntity<PhaseDto> createPhase(@Valid @RequestBody PhaseDto dto) {
        log.info("POST /api/phases {}", dto);
        PhaseDto created = phaseService.createPhase(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ADMINISTRATEUR_SYSTEME')")
    @Operation(summary = "Mettre à jour une phase")
    public ResponseEntity<PhaseDto> updatePhase(@PathVariable Long id, @Valid @RequestBody PhaseDto dto) {
        log.info("PUT /api/phases/{}", id);
        PhaseDto updated = phaseService.updatePhase(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ADMINISTRATEUR_SYSTEME')")
    @Operation(summary = "Supprimer une phase")
    public ResponseEntity<Void> deletePhase(@PathVariable Long id) {
        log.info("DELETE /api/phases/{}", id);
        phaseService.deletePhase(id);
        return ResponseEntity.noContent().build();
    }
}

