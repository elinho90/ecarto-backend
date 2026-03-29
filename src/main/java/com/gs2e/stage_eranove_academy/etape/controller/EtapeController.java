package com.gs2e.stage_eranove_academy.etape.controller;

import com.gs2e.stage_eranove_academy.etape.dto.EtapeDto;
import com.gs2e.stage_eranove_academy.etape.service.EtapeService;
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
@RequestMapping("/api/v1/etapes")
@Tag(name = "Étapes", description = "API de gestion des étapes de projet")
@Slf4j
@RequiredArgsConstructor
public class EtapeController {

    private final EtapeService etapeService;

    @GetMapping("/phase/{phaseId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Lister les étapes d'une phase")
    public ResponseEntity<List<EtapeDto>> getEtapesByPhase(@PathVariable Long phaseId) {
        log.info("GET /api/etapes/phase/{}", phaseId);
        return ResponseEntity.ok(etapeService.getEtapesByPhase(phaseId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer une étape par son ID")
    public ResponseEntity<EtapeDto> getEtapeById(@PathVariable Long id) {
        log.info("GET /api/etapes/{}", id);
        return ResponseEntity.ok(etapeService.getEtapeById(id));
    }

    @GetMapping("/responsable/{userId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Lister les étapes assignées à un responsable")
    public ResponseEntity<List<EtapeDto>> getEtapesByResponsable(@PathVariable Long userId) {
        log.info("GET /api/etapes/responsable/{}", userId);
        return ResponseEntity.ok(etapeService.getEtapesByResponsable(userId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ADMINISTRATEUR_SYSTEME')")
    @Operation(summary = "Créer une nouvelle étape")
    public ResponseEntity<EtapeDto> createEtape(@Valid @RequestBody EtapeDto dto) {
        log.info("POST /api/etapes {}", dto);
        EtapeDto created = etapeService.createEtape(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ADMINISTRATEUR_SYSTEME')")
    @Operation(summary = "Mettre à jour une étape")
    public ResponseEntity<EtapeDto> updateEtape(@PathVariable Long id, @Valid @RequestBody EtapeDto dto) {
        log.info("PUT /api/etapes/{}", id);
        EtapeDto updated = etapeService.updateEtape(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ADMINISTRATEUR_SYSTEME')")
    @Operation(summary = "Supprimer une étape")
    public ResponseEntity<Void> deleteEtape(@PathVariable Long id) {
        log.info("DELETE /api/etapes/{}", id);
        etapeService.deleteEtape(id);
        return ResponseEntity.noContent().build();
    }
}

