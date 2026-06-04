package com.gs2e.stage_eranove_academy.suivi.controller;

import com.gs2e.stage_eranove_academy.alerte.dto.AlerteDto;
import com.gs2e.stage_eranove_academy.alerte.service.AlerteService;
import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.etape.dto.EtapeDto;
import com.gs2e.stage_eranove_academy.etape.service.EtapeService;
import com.gs2e.stage_eranove_academy.historique.dto.HistoriqueStatutDto;
import com.gs2e.stage_eranove_academy.historique.service.HistoriqueStatutService;
import com.gs2e.stage_eranove_academy.phase.dto.PhaseDto;
import com.gs2e.stage_eranove_academy.phase.service.PhaseService;
import com.gs2e.stage_eranove_academy.projet.model.Projet;
import com.gs2e.stage_eranove_academy.suivi.service.SuiviProjetService;
import com.gs2e.stage_eranove_academy.validation.dto.ValidationEtapeDto;
import com.gs2e.stage_eranove_academy.validation.model.DecisionValidation;
import com.gs2e.stage_eranove_academy.validation.service.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/suivi")
@Tag(name = "Suivi Temps Réel", description = "API de suivi en temps réel des projets, phases, étapes et validations")
@Slf4j
@RequiredArgsConstructor
public class SuiviController {

    private final SuiviProjetService suiviService;
    private final PhaseService phaseService;
    private final EtapeService etapeService;
    private final AlerteService alerteService;
    private final HistoriqueStatutService historiqueStatutService;
    private final ValidationService validationService;

    // ═══════════════════════════════════════════════
    // PHASES
    // ═══════════════════════════════════════════════

    @GetMapping("/projets/{projetId}/phases")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Obtenir toutes les phases d'un projet avec leurs étapes")
    public ResponseEntity<List<PhaseDto>> getPhasesProjet(@PathVariable Long projetId) {
        log.info("GET /api/suivi/projets/{}/phases", projetId);
        return ResponseEntity.ok(phaseService.getPhasesByProjet(projetId));
    }

    @PostMapping("/projets/{projetId}/phases")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET')")
    @Operation(summary = "Créer une phase pour un projet")
    public ResponseEntity<PhaseDto> createPhase(@PathVariable Long projetId, @Valid @RequestBody PhaseDto dto) {
        log.info("POST /api/suivi/projets/{}/phases - Création phase: {}", projetId, dto.getNom());
        // Propage l'ID du projet dans le DTO puis délègue au service
        dto.setProjetId(projetId);
        PhaseDto created = phaseService.createPhase(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ═══════════════════════════════════════════════
    // ÉTAPES
    // ═══════════════════════════════════════════════

    @GetMapping("/phases/{phaseId}/etapes")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Obtenir toutes les étapes d'une phase")
    public ResponseEntity<List<EtapeDto>> getEtapesPhase(@PathVariable Long phaseId) {
        log.info("GET /api/suivi/phases/{}/etapes", phaseId);
        return ResponseEntity.ok(etapeService.getEtapesByPhase(phaseId));
    }

    @PostMapping("/phases/{phaseId}/etapes")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET')")
    @Operation(summary = "Créer une étape dans une phase")
    public ResponseEntity<EtapeDto> createEtape(@PathVariable Long phaseId, @Valid @RequestBody EtapeDto dto) {
        log.info("POST /api/suivi/phases/{}/etapes - Création étape: {}", phaseId, dto.getNom());
        dto.setPhaseId(phaseId);
        EtapeDto created = etapeService.createEtape(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/etapes/{etapeId}/demarrer")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR')")
    @Operation(summary = "Démarrer une étape (passer à EN_COURS)")
    public ResponseEntity<EtapeDto> demarrerEtape(@PathVariable Long etapeId) {
        log.info("PATCH /api/suivi/etapes/{}/demarrer", etapeId);
        EtapeDto result = etapeService.demarrerEtape(etapeId);
        return ResponseEntity.ok(result);
    }

    // ═══════════════════════════════════════════════
    // SOUMISSION & VALIDATION
    // ═══════════════════════════════════════════════

    @PostMapping("/etapes/{etapeId}/soumettre")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR')")
    @Operation(summary = "Soumettre une étape pour validation")
    public ResponseEntity<EtapeDto> soumettreEtape(
            @PathVariable Long etapeId,
            @RequestParam Long utilisateurId,
            @RequestParam(required = false) String urlLivrable) {
        log.info("POST /api/suivi/etapes/{}/soumettre par utilisateur {}", etapeId, utilisateurId);
        EtapeDto result = suiviService.soumettreEtapePourValidation(etapeId, utilisateurId, urlLivrable);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/etapes/{etapeId}/valider")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Valider ou rejeter une étape")
    public ResponseEntity<EtapeDto> validerEtape(
            @PathVariable Long etapeId,
            @RequestParam Long validateurId,
            @RequestParam DecisionValidation decision,
            @RequestParam(required = false) String commentaire) {
        log.info("POST /api/suivi/etapes/{}/valider - {} par {}", etapeId, decision, validateurId);
        EtapeDto result = suiviService.validerEtape(etapeId, validateurId, decision, commentaire);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/etapes/{etapeId}/validations")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Obtenir l'historique des validations d'une étape")
    public ResponseEntity<List<ValidationEtapeDto>> getValidationsEtape(@PathVariable Long etapeId) {
        log.info("GET /api/suivi/etapes/{}/validations", etapeId);
        return ResponseEntity.ok(validationService.getValidationsByEtape(etapeId));
    }

    // ═══════════════════════════════════════════════
    // SUIVI & ALERTES
    // ═══════════════════════════════════════════════

    @GetMapping("/projets/{projetId}/resume")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Obtenir le résumé de suivi temps réel d'un projet")
    public ResponseEntity<Map<String, Object>> getSuiviProjet(@PathVariable Long projetId) {
        log.info("GET /api/suivi/projets/{}/resume", projetId);
        return ResponseEntity.ok(suiviService.getSuiviProjet(projetId));
    }

    @GetMapping("/projets/{projetId}/alertes")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DECIDEUR')")
    @Operation(summary = "Obtenir les alertes actives d'un projet")
    public ResponseEntity<List<AlerteDto>> getAlertesProjet(@PathVariable Long projetId) {
        log.info("GET /api/suivi/projets/{}/alertes", projetId);
        return ResponseEntity.ok(alerteService.getUnresolvedAlertesByProjet(projetId));
    }

    @PatchMapping("/alertes/{alerteId}/lire")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR')")
    @Operation(summary = "Marquer une alerte comme lue")
    public ResponseEntity<AlerteDto> marquerAlerteLue(@PathVariable Long alerteId) {
        log.info("PATCH /api/suivi/alertes/{}/lire", alerteId);
        return ResponseEntity.ok(alerteService.markAsRead(alerteId));
    }

    @PatchMapping("/alertes/{alerteId}/resoudre")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET')")
    @Operation(summary = "Marquer une alerte comme résolue")
    public ResponseEntity<AlerteDto> marquerAlerteResolue(@PathVariable Long alerteId) {
        log.info("PATCH /api/suivi/alertes/{}/resoudre", alerteId);
        return ResponseEntity.ok(alerteService.markAsResolved(alerteId));
    }

    @GetMapping("/projets/{projetId}/historique")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Obtenir l'historique des changements de statut d'un projet")
    public ResponseEntity<List<HistoriqueStatutDto>> getHistoriqueStatuts(@PathVariable Long projetId) {
        log.info("GET /api/suivi/projets/{}/historique", projetId);
        return ResponseEntity.ok(historiqueStatutService.getHistoriqueByProjet(projetId));
    }

    @PostMapping("/projets/{projetId}/statut")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Changer le statut d'un projet avec contrôle et historique")
    public ResponseEntity<?> changerStatutProjet(
            @PathVariable Long projetId,
            @RequestParam Projet.StatutProjet nouveauStatut,
            @RequestParam Long utilisateurId,
            @RequestParam(required = false) String motif) {
        log.info("POST /api/suivi/projets/{}/statut -> {} par {}", projetId, nouveauStatut, utilisateurId);
        Projet updated = suiviService.changerStatutProjet(projetId, nouveauStatut, utilisateurId, motif);
        return ResponseEntity.ok(Map.of(
                "projetId", updated.getId(),
                "nouveauStatut", updated.getStatut(),
                "progression", updated.getProgression()
        ));
    }

    @GetMapping("/projets/{projetId}/peut-passer-recette")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET')")
    @Operation(summary = "Vérifier si un projet peut passer en recette (toutes étapes bloquantes validées)")
    public ResponseEntity<Map<String, Object>> peutPasserRecette(@PathVariable Long projetId) {
        log.info("GET /api/suivi/projets/{}/peut-passer-recette", projetId);
        boolean peut = suiviService.peutPasserEnRecette(projetId);
        return ResponseEntity.ok(Map.of("peutPasserEnRecette", peut, "projetId", projetId));
    }

    @GetMapping("/phases/{phaseId}/bilan")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Générer le bilan automatique d'une phase (statistiques de complétion)")
    public ResponseEntity<Map<String, Object>> getBilanPhase(@PathVariable Long phaseId) {
        log.info("GET /api/suivi/phases/{}/bilan", phaseId);
        return ResponseEntity.ok(suiviService.genererBilanPhase(phaseId));
    }
}
