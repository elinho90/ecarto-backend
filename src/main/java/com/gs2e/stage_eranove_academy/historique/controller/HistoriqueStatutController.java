package com.gs2e.stage_eranove_academy.historique.controller;

import com.gs2e.stage_eranove_academy.historique.dto.HistoriqueStatutDto;
import com.gs2e.stage_eranove_academy.historique.service.HistoriqueStatutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/historique-statuts")
@Tag(name = "Historique des statuts", description = "API de consultation de l'historique des changements de statut des projets")
@Slf4j
@RequiredArgsConstructor
public class HistoriqueStatutController {

    private final HistoriqueStatutService historiqueStatutService;

    @GetMapping("/projet/{projetId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Lister l'historique des statuts pour un projet spécifique")
    public ResponseEntity<List<HistoriqueStatutDto>> getHistoriqueByProjet(@PathVariable Long projetId) {
        log.info("GET /api/historique-statuts/projet/{}", projetId);
        return ResponseEntity.ok(historiqueStatutService.getHistoriqueByProjet(projetId));
    }
}
