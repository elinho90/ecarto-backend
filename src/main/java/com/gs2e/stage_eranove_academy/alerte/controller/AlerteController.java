package com.gs2e.stage_eranove_academy.alerte.controller;

import com.gs2e.stage_eranove_academy.alerte.dto.AlerteDto;
import com.gs2e.stage_eranove_academy.alerte.service.AlerteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alertes")
@Tag(name = "Alertes", description = "API de gestion des alertes du système de suivi")
@Slf4j
@RequiredArgsConstructor
public class AlerteController {

    private final AlerteService alerteService;

    @GetMapping("/projet/{projetId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Lister toutes les alertes d'un projet")
    public ResponseEntity<List<AlerteDto>> getAlertesByProjet(@PathVariable Long projetId) {
        log.info("GET /api/alertes/projet/{}", projetId);
        return ResponseEntity.ok(alerteService.getAlertesByProjet(projetId));
    }

    @GetMapping("/projet/{projetId}/non-resolues")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Lister les alertes non résolues d'un projet")
    public ResponseEntity<List<AlerteDto>> getUnresolvedAlertesByProjet(@PathVariable Long projetId) {
        log.info("GET /api/alertes/projet/{}/non-resolues", projetId);
        return ResponseEntity.ok(alerteService.getUnresolvedAlertesByProjet(projetId));
    }

    @GetMapping("/destinataire/{userId}/non-lues")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Lister les alertes non lues pour un utilisateur")
    public ResponseEntity<List<AlerteDto>> getUnreadAlertesByDestinataire(@PathVariable Long userId) {
        log.info("GET /api/alertes/destinataire/{}/non-lues", userId);
        return ResponseEntity.ok(alerteService.getUnreadAlertesByDestinataire(userId));
    }

    @GetMapping("/destinataire/{userId}/non-lues/count")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Compter les alertes non lues pour un utilisateur")
    public ResponseEntity<Long> countUnreadAlertesByDestinataire(@PathVariable Long userId) {
        log.info("GET /api/alertes/destinataire/{}/non-lues/count", userId);
        return ResponseEntity.ok(alerteService.countUnreadAlertesByDestinataire(userId));
    }

    @PutMapping("/{id}/lire")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Marquer une alerte comme lue")
    public ResponseEntity<AlerteDto> markAsRead(@PathVariable Long id) {
        log.info("PUT /api/alertes/{}/lire", id);
        return ResponseEntity.ok(alerteService.markAsRead(id));
    }

    @PutMapping("/{id}/resoudre")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ADMINISTRATEUR_SYSTEME')")
    @Operation(summary = "Marquer une alerte comme résolue")
    public ResponseEntity<AlerteDto> markAsResolved(@PathVariable Long id) {
        log.info("PUT /api/alertes/{}/resoudre", id);
        return ResponseEntity.ok(alerteService.markAsResolved(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_SYSTEME')")
    @Operation(summary = "Supprimer une alerte")
    public ResponseEntity<Void> deleteAlerte(@PathVariable Long id) {
        log.info("DELETE /api/alertes/{}", id);
        alerteService.deleteAlerte(id);
        return ResponseEntity.noContent().build();
    }
}

