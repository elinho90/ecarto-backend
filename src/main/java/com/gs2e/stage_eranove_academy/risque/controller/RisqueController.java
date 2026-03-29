package com.gs2e.stage_eranove_academy.risque.controller;

import com.gs2e.stage_eranove_academy.risque.dto.RisqueDto;
import com.gs2e.stage_eranove_academy.risque.service.RisqueService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/risques")
public class RisqueController {

    private final RisqueService risqueService;

    public RisqueController(RisqueService risqueService) {
        this.risqueService = risqueService;
    }

    @GetMapping("/projet/{projetId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR', 'ANALYSTE')")
    @Operation(summary = "Lister les risques d'un projet")
    public ResponseEntity<List<RisqueDto>> getRisquesByProjet(@PathVariable Long projetId) {
        return ResponseEntity.ok(risqueService.findAllByProjet(projetId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET')")
    @Operation(summary = "Créer un nouveau risque sur un projet")
    public ResponseEntity<RisqueDto> addRisque(@Valid @RequestBody RisqueDto dto) {
        return new ResponseEntity<>(risqueService.createRisque(dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE')")
    @Operation(summary = "Mettre à jour le statut d'un risque")
    public ResponseEntity<RisqueDto> updateStatutRisque(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(risqueService.updateStatut(id, statut));
    }
}
