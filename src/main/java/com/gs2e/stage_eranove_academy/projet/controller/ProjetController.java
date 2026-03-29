package com.gs2e.stage_eranove_academy.projet.controller;

import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.common.Exceptions.InvalidOperationException;
import com.gs2e.stage_eranove_academy.projet.dto.ProjetDto;
import com.gs2e.stage_eranove_academy.projet.model.Projet;
import com.gs2e.stage_eranove_academy.projet.service.ProjetService;
import com.gs2e.stage_eranove_academy.rapport.service.RapportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/projets")
@Tag(name = "Projets", description = "API de gestion des projets")
@Slf4j
public class ProjetController {

    private final ProjetService projetService;
    private final RapportService rapportService;

    @Autowired
    public ProjetController(ProjetService projetService, RapportService rapportService) {
        this.projetService = projetService;
        this.rapportService = rapportService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer tous les projets", description = "Récupère la liste paginée de tous les projets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Page<ProjetDto>> getAllProjects(
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("GET /api/projets - Récupération de tous les projets");
        Page<ProjetDto> projects = projetService.getAllProjects(pageable);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer un projet par ID", description = "Récupère un projet spécifique par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projet trouvé"),
            @ApiResponse(responseCode = "404", description = "Projet non trouvé")
    })
    public ResponseEntity<ProjetDto> getProjectById(
            @Parameter(description = "ID du projet") @PathVariable Long id) throws EntityNotFoundException {
        log.info("GET /api/projets/{} - Récupération du projet", id);
        ProjetDto projet = projetService.getProjectById(id);
        return ResponseEntity.ok(projet);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET')")
    @Operation(summary = "Créer un nouveau projet", description = "Crée un nouveau projet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Projet créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Conflit - Projet existe déjà")
    })
    public ResponseEntity<ProjetDto> createProject(
            @Valid @RequestBody ProjetDto projetDto) throws InvalidOperationException {
        log.info("POST /api/projets - Création d'un nouveau projet");
        ProjetDto createdProjet = projetService.createProject(projetDto);
        return new ResponseEntity<>(createdProjet, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET')")
    @Operation(summary = "Mettre à jour un projet", description = "Met à jour un projet existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projet mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Projet non trouvé")
    })
    public ResponseEntity<ProjetDto> updateProject(
            @Parameter(description = "ID du projet") @PathVariable Long id,
            @Valid @RequestBody ProjetDto projetDto) throws EntityNotFoundException, InvalidOperationException {
        log.info("PUT /api/projets/{} - Mise à jour du projet", id);
        ProjetDto updatedProjet = projetService.updateProject(id, projetDto);
        return ResponseEntity.ok(updatedProjet);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_SYSTEME')")
    @Operation(summary = "Supprimer un projet", description = "Supprime un projet existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Projet supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Projet non trouvé")
    })
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "ID du projet") @PathVariable Long id) throws EntityNotFoundException {
        log.info("DELETE /api/projets/{} - Suppression du projet", id);
        projetService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/report")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Générer le rapport PDF", description = "Génère un rapport PDF stylisé du projet")
    public ResponseEntity<Resource> getProjectReport(@PathVariable Long id)
            throws EntityNotFoundException {
        log.info("GET /api/projets/{}/report - Génération PDF", id);
        var bis = rapportService.generateProjectReport(id);
        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=rapport_projet_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @PostMapping("/{id}/report/send")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Envoyer le rapport par email", description = "Envoie le rapport PDF par email")
    public ResponseEntity<?> sendProjectReport(@PathVariable Long id, @RequestParam String email) {
        log.info("POST /api/projets/{}/report/send - Envoi email à {}", id, email);
        try {
            rapportService.sendProjectReportByEmail(id, email);
            return ResponseEntity.ok(Map.of("message", "Rapport envoyé avec succès à " + email));
        } catch (Exception e) {
            log.error("Erreur d'envoi d'email: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Échec de l'envoi de l'email",
                    "message",
                    "Veuillez vérifier votre configuration SMTP dans application.properties (Serveur, identifiants ou mot de passe d'application)."));
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Rechercher des projets", description = "Recherche multicritères de projets")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<Page<ProjetDto>> searchProjects(
            @Parameter(description = "Nom du projet") @RequestParam(required = false) String nom,
            @Parameter(description = "Statut du projet") @RequestParam(required = false) Projet.StatutProjet statut,
            @Parameter(description = "Responsable du projet") @RequestParam(required = false) String responsable,
            @Parameter(description = "ID du type de projet") @RequestParam(required = false) Long typeProjetId,
            @Parameter(description = "Date de début (min)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebutFrom,
            @Parameter(description = "Date de début (max)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebutTo,
            @Parameter(description = "Budget minimum") @RequestParam(required = false) BigDecimal budgetMin,
            @Parameter(description = "Budget maximum") @RequestParam(required = false) BigDecimal budgetMax,
            @PageableDefault(size = 10) Pageable pageable) {

        log.info("GET /api/projets/search - Recherche de projets avec critères");
        Page<ProjetDto> projects = projetService.searchProjects(nom, statut, responsable, typeProjetId,
                dateDebutFrom, dateDebutTo, budgetMin, budgetMax, pageable);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer des projets par statut", description = "Récupère tous les projets avec un statut spécifique")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<Page<ProjetDto>> getProjectsByStatus(
            @Parameter(description = "Statut du projet") @PathVariable Projet.StatutProjet status,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("GET /api/projets/status/{} - Récupération des projets par statut", status);
        Page<ProjetDto> projects = projetService.getProjectsByStatus(status, pageable);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/responsable/{responsable}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR')")
    @Operation(summary = "Récupérer des projets par responsable", description = "Récupère tous les projets d'un responsable spécifique")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<Page<ProjetDto>> getProjectsByResponsable(
            @Parameter(description = "Responsable du projet") @PathVariable String responsable,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("GET /api/projets/responsable/{} - Récupération des projets par responsable", responsable);
        Page<ProjetDto> projects = projetService.getProjectsByResponsable(responsable, pageable);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/type-projet/{typeProjetId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer des projets par type", description = "Récupère tous les projets d'un type spécifique")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<Page<ProjetDto>> getProjectsByTypeProjet(
            @Parameter(description = "ID du type de projet") @PathVariable Long typeProjetId,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("GET /api/projets/type-projet/{} - Récupération des projets par type", typeProjetId);
        Page<ProjetDto> projects = projetService.getProjectsByTypeProjet(typeProjetId, pageable);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/delayed")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Récupérer les projets en retard", description = "Récupère les projets en cours qui sont en retard")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<List<ProjetDto>> getDelayedProjects() {
        log.info("GET /api/projets/delayed - Récupération des projets en retard");
        List<ProjetDto> delayedProjects = projetService.getDelayedProjects();
        return ResponseEntity.ok(delayedProjects);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Récupérer les statistiques", description = "Récupère les statistiques globales des projets")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<Map<String, Object>> getProjectStatistics() {
        log.info("GET /api/projets/statistics - Récupération des statistiques");
        Map<String, Object> statistics = projetService.getProjectStatistics();
        return ResponseEntity.ok(statistics);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Mettre à jour le statut d'un projet", description = "Met à jour uniquement le statut d'un projet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Projet non trouvé")
    })
    public ResponseEntity<ProjetDto> updateProjectStatus(
            @Parameter(description = "ID du projet") @PathVariable Long id,
            @Parameter(description = "Nouveau statut") @RequestParam Projet.StatutProjet status)
            throws EntityNotFoundException {
        log.info("PATCH /api/projets/{}/status - Mise à jour du statut", id);
        ProjetDto updatedProjet = projetService.updateProjectStatus(id, status);
        return ResponseEntity.ok(updatedProjet);
    }

    @PatchMapping("/{id}/progress")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR')")
    @Operation(summary = "Mettre à jour la progression d'un projet", description = "Met à jour uniquement la progression d'un projet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Progression mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Projet non trouvé")
    })
    public ResponseEntity<ProjetDto> updateProjectProgress(
            @Parameter(description = "ID du projet") @PathVariable Long id,
            @Parameter(description = "Nouvelle progression") @RequestParam @Min(0) @Max(100) Integer progress)
            throws EntityNotFoundException {
        log.info("PATCH /api/projets/{}/progress - Mise à jour de la progression", id);
        ProjetDto updatedProjet = projetService.updateProjectProgress(id, progress);
        return ResponseEntity.ok(updatedProjet);
    }

    @GetMapping("/team-member/{membre}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR')")
    @Operation(summary = "Récupérer des projets par membre d'équipe", description = "Récupère tous les projets où le membre fait partie de l'équipe")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<Page<ProjetDto>> getProjectsByTeamMember(
            @Parameter(description = "Membre de l'équipe") @PathVariable String membre,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("GET /api/projets/team-member/{} - Récupération des projets par membre d'équipe", membre);
        Page<ProjetDto> projects = projetService.getProjectsByTeamMember(membre, pageable);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/ending-soon")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR')")
    @Operation(summary = "Récupérer les projets se terminant bientôt", description = "Récupère les projets se terminant dans les prochains jours")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<List<ProjetDto>> getProjectsEndingSoon(
            @Parameter(description = "Nombre de jours") @RequestParam(defaultValue = "30") int days) {
        log.info("GET /api/projets/ending-soon - Récupération des projets se terminant bientôt");
        List<ProjetDto> projects = projetService.getProjectsEndingSoon(days);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/budget-statistics")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Récupérer les statistiques de budget", description = "Récupère les statistiques de budget par statut")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<Map<String, BigDecimal>> getBudgetStatisticsByStatus() {
        log.info("GET /api/projets/budget-statistics - Récupération des statistiques de budget");
        Map<String, BigDecimal> statistics = projetService.getBudgetStatisticsByStatus();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/evolution")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Récupérer l'évolution des projets", description = "Récupère les statistiques d'évolution des projets par mois")
    public ResponseEntity<List<Map<String, Object>>> getProjectEvolution() {
        log.info("GET /api/projets/evolution - Récupération de l'évolution des projets");
        List<Map<String, Object>> evolution = projetService.getProjectEvolution();
        return ResponseEntity.ok(evolution);
    }

    @GetMapping("/stats-by-type")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Récupérer les statistiques par type", description = "Récupère le nombre de projets par type")
    public ResponseEntity<Map<String, Long>> getProjectsByTypeStats() {
        log.info("GET /api/projets/stats-by-type - Récupération des stats par type");
        Map<String, Long> stats = projetService.getProjectsByTypeStats();
        return ResponseEntity.ok(stats);
    }
}
