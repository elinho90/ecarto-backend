package com.gs2e.stage_eranove_academy.suivi.controller;

import com.gs2e.stage_eranove_academy.alerte.dto.AlerteDto;
import com.gs2e.stage_eranove_academy.alerte.model.Alerte;
import com.gs2e.stage_eranove_academy.alerte.repository.AlerteRepository;
import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.etape.dto.EtapeDto;
import com.gs2e.stage_eranove_academy.etape.model.Etape;
import com.gs2e.stage_eranove_academy.etape.model.StatutEtape;
import com.gs2e.stage_eranove_academy.etape.repository.EtapeRepository;
import com.gs2e.stage_eranove_academy.historique.model.HistoriqueStatut;
import com.gs2e.stage_eranove_academy.historique.repository.HistoriqueStatutRepository;
import com.gs2e.stage_eranove_academy.phase.dto.PhaseDto;
import com.gs2e.stage_eranove_academy.phase.model.Phase;
import com.gs2e.stage_eranove_academy.phase.repository.PhaseRepository;
import com.gs2e.stage_eranove_academy.projet.model.Projet;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import com.gs2e.stage_eranove_academy.security.repository.UtilisateurRepository;
import com.gs2e.stage_eranove_academy.suivi.service.SuiviProjetService;
import com.gs2e.stage_eranove_academy.validation.dto.ValidationEtapeDto;
import com.gs2e.stage_eranove_academy.validation.model.DecisionValidation;
import com.gs2e.stage_eranove_academy.validation.model.ValidationEtape;
import com.gs2e.stage_eranove_academy.validation.repository.ValidationEtapeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/suivi")
@Tag(name = "Suivi Temps Réel", description = "API de suivi en temps réel des projets, phases, étapes et validations")
@Slf4j
@RequiredArgsConstructor
public class SuiviController {

    private final SuiviProjetService suiviService;
    private final PhaseRepository phaseRepository;
    private final EtapeRepository etapeRepository;
    private final ValidationEtapeRepository validationRepository;
    private final AlerteRepository alerteRepository;
    private final HistoriqueStatutRepository historiqueRepository;
    private final UtilisateurRepository utilisateurRepository;

    // ═══════════════════════════════════════════════
    // PHASES
    // ═══════════════════════════════════════════════

    @GetMapping("/projets/{projetId}/phases")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Obtenir toutes les phases d'un projet avec leurs étapes")
    public ResponseEntity<List<PhaseDto>> getPhasesProjet(@PathVariable Long projetId) {
        log.info("GET /api/suivi/projets/{}/phases", projetId);
        List<Phase> phases = phaseRepository.findByProjetIdOrderByOrdre(projetId);
        List<PhaseDto> dtos = phases.stream().map(this::mapPhaseToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/projets/{projetId}/phases")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET')")
    @Operation(summary = "Créer une phase pour un projet")
    public ResponseEntity<PhaseDto> createPhase(@PathVariable Long projetId, @Valid @RequestBody PhaseDto dto) {
        log.info("POST /api/suivi/projets/{}/phases - Création phase: {}", projetId, dto.getNom());
        Phase phase = new Phase();
        phase.setNom(dto.getNom());
        phase.setDescription(dto.getDescription());
        phase.setOrdre(dto.getOrdre() != null ? dto.getOrdre() : 1);
        phase.setDateDebutPrevue(dto.getDateDebutPrevue());
        phase.setDateFinPrevue(dto.getDateFinPrevue());
        phase.setVerrouillee(dto.getVerrouillee() != null ? dto.getVerrouillee() : false);

        Projet projet = new Projet();
        projet.setId(projetId);
        phase.setProjet(projet);

        Phase saved = phaseRepository.save(phase);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapPhaseToDto(saved));
    }

    // ═══════════════════════════════════════════════
    // ÉTAPES
    // ═══════════════════════════════════════════════

    @GetMapping("/phases/{phaseId}/etapes")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Obtenir toutes les étapes d'une phase")
    public ResponseEntity<List<EtapeDto>> getEtapesPhase(@PathVariable Long phaseId) {
        log.info("GET /api/suivi/phases/{}/etapes", phaseId);
        List<Etape> etapes = etapeRepository.findByPhaseIdOrderByOrdre(phaseId);
        List<EtapeDto> dtos = etapes.stream().map(this::mapEtapeToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/phases/{phaseId}/etapes")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET')")
    @Operation(summary = "Créer une étape dans une phase")
    public ResponseEntity<EtapeDto> createEtape(@PathVariable Long phaseId, @Valid @RequestBody EtapeDto dto) {
        log.info("POST /api/suivi/phases/{}/etapes - Création étape: {}", phaseId, dto.getNom());
        Phase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new EntityNotFoundException("Phase non trouvée avec l'ID: " + phaseId));

        Etape etape = new Etape();
        etape.setNom(dto.getNom());
        etape.setDescription(dto.getDescription());
        etape.setOrdre(dto.getOrdre() != null ? dto.getOrdre() : 1);
        etape.setPhase(phase);
        etape.setDateEcheance(dto.getDateEcheance());
        etape.setDureeEstimeeJours(dto.getDureeEstimeeJours());
        etape.setValidationRequise(dto.getValidationRequise() != null ? dto.getValidationRequise() : true);
        etape.setBloquante(dto.getBloquante() != null ? dto.getBloquante() : false);
        etape.setTypeLivrable(dto.getTypeLivrable());

        if (dto.getResponsableId() != null) {
            Utilisateur responsable = utilisateurRepository.findById(dto.getResponsableId())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
            etape.setResponsable(responsable);
        }

        Etape saved = etapeRepository.save(etape);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapEtapeToDto(saved));
    }

    @PatchMapping("/etapes/{etapeId}/demarrer")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR')")
    @Operation(summary = "Démarrer une étape (passer à EN_COURS)")
    public ResponseEntity<EtapeDto> demarrerEtape(@PathVariable Long etapeId) {
        log.info("PATCH /api/suivi/etapes/{}/demarrer", etapeId);
        Etape etape = etapeRepository.findById(etapeId)
                .orElseThrow(() -> new EntityNotFoundException("Étape non trouvée"));
        etape.setStatut(StatutEtape.EN_COURS);
        Etape saved = etapeRepository.save(etape);
        return ResponseEntity.ok(mapEtapeToDto(saved));
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
        List<ValidationEtape> validations = validationRepository.findByEtapeIdOrderByDateValidationDesc(etapeId);
        List<ValidationEtapeDto> dtos = validations.stream()
                .map(this::mapValidationToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ═══════════════════════════════════════════════
    // SUIVI & ALERTES
    // ═══════════════════════════════════════════════

    @GetMapping("/projets/{projetId}/resume")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Obtenir le résumé de suivi temps réel d'un projet")
    public ResponseEntity<Map<String, Object>> getSuiviProjet(@PathVariable Long projetId) {
        log.info("GET /api/suivi/projets/{}/resume", projetId);
        Map<String, Object> suivi = suiviService.getSuiviProjet(projetId);
        return ResponseEntity.ok(suivi);
    }

    @GetMapping("/projets/{projetId}/alertes")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DECIDEUR')")
    @Operation(summary = "Obtenir les alertes actives d'un projet")
    public ResponseEntity<List<AlerteDto>> getAlertesProjet(@PathVariable Long projetId) {
        log.info("GET /api/suivi/projets/{}/alertes", projetId);
        List<Alerte> alertes = alerteRepository.findByProjetIdAndResolueFalseOrderByCreatedAtDesc(projetId);
        List<AlerteDto> dtos = alertes.stream().map(this::mapAlerteToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/alertes/{alerteId}/lire")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR')")
    @Operation(summary = "Marquer une alerte comme lue")
    public ResponseEntity<Void> marquerAlerteLue(@PathVariable Long alerteId) {
        Alerte alerte = alerteRepository.findById(alerteId)
                .orElseThrow(() -> new EntityNotFoundException("Alerte non trouvée"));
        alerte.setLue(true);
        alerteRepository.save(alerte);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/alertes/{alerteId}/resoudre")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET')")
    @Operation(summary = "Marquer une alerte comme résolue")
    public ResponseEntity<Void> marquerAlerteResolue(@PathVariable Long alerteId) {
        Alerte alerte = alerteRepository.findById(alerteId)
                .orElseThrow(() -> new EntityNotFoundException("Alerte non trouvée"));
        alerte.setResolue(true);
        alerteRepository.save(alerte);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/projets/{projetId}/historique")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Obtenir l'historique des changements de statut d'un projet")
    public ResponseEntity<List<HistoriqueStatut>> getHistoriqueStatuts(@PathVariable Long projetId) {
        log.info("GET /api/suivi/projets/{}/historique", projetId);
        return ResponseEntity.ok(historiqueRepository.findByProjetIdOrderByDateChangementDesc(projetId));
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
        boolean peut = suiviService.peutPasserEnRecette(projetId);
        return ResponseEntity.ok(Map.of("peutPasserEnRecette", peut, "projetId", projetId));
    }

    @GetMapping("/phases/{phaseId}/bilan")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Générer le bilan automatique d'une phase (statistiques de complétion)")
    public ResponseEntity<Map<String, Object>> getBilanPhase(@PathVariable Long phaseId) {
        log.info("GET /api/suivi/phases/{}/bilan", phaseId);
        Map<String, Object> bilan = suiviService.genererBilanPhase(phaseId);
        return ResponseEntity.ok(bilan);
    }

    // ═══════════════════════════════════════════════
    // MAPPERS LOCAUX
    // ═══════════════════════════════════════════════

    private PhaseDto mapPhaseToDto(Phase phase) {
        PhaseDto dto = new PhaseDto();
        dto.setId(phase.getId());
        dto.setNom(phase.getNom());
        dto.setDescription(phase.getDescription());
        dto.setOrdre(phase.getOrdre());
        dto.setProjetId(phase.getProjet().getId());
        dto.setDateDebutPrevue(phase.getDateDebutPrevue());
        dto.setDateFinPrevue(phase.getDateFinPrevue());
        dto.setDateDebutReelle(phase.getDateDebutReelle());
        dto.setDateFinReelle(phase.getDateFinReelle());
        dto.setProgression(phase.getProgression());
        dto.setStatut(phase.getStatut());
        dto.setVerrouillee(phase.getVerrouillee());

        List<Etape> etapes = etapeRepository.findByPhaseIdOrderByOrdre(phase.getId());
        dto.setEtapes(etapes.stream().map(this::mapEtapeToDto).collect(Collectors.toList()));
        dto.setTotalEtapes(etapes.size());
        dto.setEtapesValidees((int) etapes.stream().filter(e -> e.getStatut() == StatutEtape.VALIDEE).count());
        dto.setEtapesEnRetard((int) etapes.stream().filter(Etape::isEnRetard).count());

        return dto;
    }

    private EtapeDto mapEtapeToDto(Etape etape) {
        EtapeDto dto = new EtapeDto();
        dto.setId(etape.getId());
        dto.setNom(etape.getNom());
        dto.setDescription(etape.getDescription());
        dto.setOrdre(etape.getOrdre());
        dto.setPhaseId(etape.getPhase().getId());
        dto.setPhaseNom(etape.getPhase().getNom());
        if (etape.getResponsable() != null) {
            dto.setResponsableId(etape.getResponsable().getId());
            dto.setResponsableNom(etape.getResponsable().getNom() + " " + etape.getResponsable().getPrenom());
        }
        dto.setDateEcheance(etape.getDateEcheance());
        dto.setDateRealisation(etape.getDateRealisation());
        dto.setDureeEstimeeJours(etape.getDureeEstimeeJours());
        dto.setDureeReelleJours(etape.getDureeReelleJours());
        dto.setStatut(etape.getStatut());
        dto.setValidationRequise(etape.getValidationRequise());
        dto.setBloquante(etape.getBloquante());
        dto.setTypeLivrable(etape.getTypeLivrable());
        dto.setUrlLivrable(etape.getUrlLivrable());
        dto.setEnRetard(etape.isEnRetard());
        dto.setJoursRetard(etape.getJoursRetard());
        dto.setJoursRestants(etape.getJoursRestants());
        return dto;
    }

    private ValidationEtapeDto mapValidationToDto(ValidationEtape v) {
        ValidationEtapeDto dto = new ValidationEtapeDto();
        dto.setId(v.getId());
        dto.setEtapeId(v.getEtape().getId());
        dto.setEtapeNom(v.getEtape().getNom());
        dto.setValidateurId(v.getValidateur().getId());
        dto.setValidateurNom(v.getValidateur().getNom() + " " + v.getValidateur().getPrenom());
        dto.setDecision(v.getDecision());
        dto.setCommentaire(v.getCommentaire());
        dto.setDateValidation(v.getDateValidation());
        dto.setPiecesJointes(v.getPiecesJointes());
        return dto;
    }

    private AlerteDto mapAlerteToDto(Alerte alerte) {
        AlerteDto dto = new AlerteDto();
        dto.setId(alerte.getId());
        if (alerte.getProjet() != null) {
            dto.setProjetId(alerte.getProjet().getId());
            dto.setProjetNom(alerte.getProjet().getNom());
        }
        if (alerte.getEtape() != null) {
            dto.setEtapeId(alerte.getEtape().getId());
            dto.setEtapeNom(alerte.getEtape().getNom());
        }
        dto.setType(alerte.getType());
        dto.setNiveau(alerte.getNiveau());
        dto.setMessage(alerte.getMessage());
        dto.setLue(alerte.getLue());
        dto.setResolue(alerte.getResolue());
        if (alerte.getDestinataire() != null) {
            dto.setDestinataireId(alerte.getDestinataire().getId());
        }
        dto.setCreatedAt(alerte.getCreatedAt());
        return dto;
    }
}
