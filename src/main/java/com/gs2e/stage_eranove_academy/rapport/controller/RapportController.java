package com.gs2e.stage_eranove_academy.rapport.controller;

import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.common.Exceptions.InvalidOperationException;
import com.gs2e.stage_eranove_academy.rapport.dto.RapportDto;
import com.gs2e.stage_eranove_academy.rapport.model.Rapport;
import com.gs2e.stage_eranove_academy.rapport.service.RapportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rapports")
@Tag(name = "Rapports", description = "API de gestion des rapports de faisabilité")
@Slf4j
@RequiredArgsConstructor
public class RapportController {

        private final RapportService rapportService;

        @GetMapping
        @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR')")
        @Operation(summary = "Récupérer tous les rapports", description = "Récupère la liste paginée de tous les rapports")
        @ApiResponse(responseCode = "200", description = "Succès")
        public ResponseEntity<Page<RapportDto>> getAllRapports(
                        @PageableDefault(size = 10) Pageable pageable) {
                log.info("GET /api/rapports - Récupération de tous les rapports");
                return ResponseEntity.ok(rapportService.getAllRapports(pageable));
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR')")
        @Operation(summary = "Récupérer un rapport par ID", description = "Récupère un rapport spécifique par son ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Rapport trouvé"),
                        @ApiResponse(responseCode = "404", description = "Rapport non trouvé")
        })
        public ResponseEntity<RapportDto> getRapportById(
                        @Parameter(description = "ID du rapport") @PathVariable Long id)
                        throws EntityNotFoundException {
                log.info("GET /api/rapports/{} - Récupération du rapport", id);
                return ResponseEntity.ok(rapportService.getRapportById(id));
        }

        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE')")
        @Operation(summary = "Uploader un rapport", description = "Upload et sauvegarde un nouveau rapport")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Rapport uploadé avec succès"),
                        @ApiResponse(responseCode = "400", description = "Données invalides"),
                        @ApiResponse(responseCode = "413", description = "Fichier trop volumineux")
        })
        public ResponseEntity<RapportDto> uploadRapport(
                        @Parameter(description = "Fichier à uploader") @RequestPart("file") MultipartFile file,
                        @Parameter(description = "Données du rapport") @Valid @RequestPart("rapport") RapportDto rapportDto,
                        @Parameter(description = "Utilisateur qui upload") @RequestParam("uploadePar") String uploadePar)
                        throws IOException, InvalidOperationException {

                log.info("POST /api/rapports - Upload d'un nouveau rapport par {}", uploadePar);
                RapportDto uploadedRapport = rapportService.uploadRapport(file, rapportDto, uploadePar);
                return ResponseEntity.status(201).body(uploadedRapport);
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE')")
        @Operation(summary = "Mettre à jour un rapport", description = "Met à jour un rapport existant")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Rapport mis à jour avec succès"),
                        @ApiResponse(responseCode = "400", description = "Données invalides"),
                        @ApiResponse(responseCode = "404", description = "Rapport non trouvé")
        })
        public ResponseEntity<RapportDto> updateRapport(
                        @Parameter(description = "ID du rapport") @PathVariable Long id,
                        @Valid @RequestBody RapportDto rapportDto)
                        throws EntityNotFoundException, InvalidOperationException {
                log.info("PUT /api/rapports/{} - Mise à jour du rapport", id);
                RapportDto updatedRapport = rapportService.updateRapport(id, rapportDto);
                return ResponseEntity.ok(updatedRapport);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMINISTRATEUR_SYSTEME')")
        @Operation(summary = "Supprimer un rapport", description = "Supprime un rapport existant")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Rapport supprimé avec succès"),
                        @ApiResponse(responseCode = "404", description = "Rapport non trouvé")
        })
        public ResponseEntity<Void> deleteRapport(
                        @Parameter(description = "ID du rapport") @PathVariable Long id)
                        throws EntityNotFoundException {
                log.info("DELETE /api/rapports/{} - Suppression du rapport", id);
                rapportService.deleteRapport(id);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/search")
        @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR')")
        @Operation(summary = "Rechercher des rapports", description = "Recherche multicritères de rapports")
        @ApiResponse(responseCode = "200", description = "Succès")
        public ResponseEntity<Page<RapportDto>> searchRapports(
                        @Parameter(description = "Nom du rapport") @RequestParam(required = false) String nom,
                        @Parameter(description = "ID du projet") @RequestParam(required = false) Long projetId,
                        @Parameter(description = "Type de fichier") @RequestParam(required = false) String fichierType,
                        @Parameter(description = "Auteur de l'upload") @RequestParam(required = false) String uploadePar,
                        @Parameter(description = "Niveau de risque") @RequestParam(required = false) Rapport.NiveauRisque risque,
                        @Parameter(description = "Faisabilité minimum") @RequestParam(required = false) Integer minFaisabilite,
                        @Parameter(description = "Faisabilité maximum") @RequestParam(required = false) Integer maxFaisabilite,
                        @Parameter(description = "Date d'upload (min)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime uploadDateFrom,
                        @Parameter(description = "Date d'upload (max)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime uploadDateTo,
                        @PageableDefault(size = 10) Pageable pageable) {

                log.info("GET /api/rapports/search - Recherche de rapports avec critères");
                Page<RapportDto> rapports = rapportService.searchRapports(nom, projetId, fichierType, uploadePar,
                                risque, minFaisabilite, maxFaisabilite, uploadDateFrom, uploadDateTo, pageable);
                return ResponseEntity.ok(rapports);
        }

        @GetMapping("/download/{id}")
        @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR')")
        @Operation(summary = "Télécharger un rapport", description = "Télécharge le fichier du rapport")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Fichier téléchargé avec succès"),
                        @ApiResponse(responseCode = "404", description = "Rapport ou fichier non trouvé")
        })
        public ResponseEntity<Resource> downloadRapport(
                        @Parameter(description = "ID du rapport") @PathVariable Long id)
                        throws EntityNotFoundException, IOException {
                log.info("GET /api/rapports/download/{} - Téléchargement du rapport", id);

                byte[] data;
                try {
                        // Essayer de récupérer le fichier physique
                        data = rapportService.downloadRapport(id);
                } catch (EntityNotFoundException e) {
                        // Fallback : générer un PDF à la volée si le fichier n'existe pas
                        log.info("Fichier physique non trouvé pour le rapport {}, génération d'un PDF à la volée", id);
                        data = rapportService.generatePDFReport(id);
                }

                ByteArrayResource resource = new ByteArrayResource(data);
                String filename = "rapport_" + id + "_" + System.currentTimeMillis() + ".pdf";

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                                .contentType(MediaType.APPLICATION_PDF)
                                .body(resource);
        }

        @PostMapping("/{id}/analyze")
        @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE')")
        @Operation(summary = "Analyser un rapport", description = "Lance l'analyse automatique d'un rapport")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Analyse effectuée avec succès"),
                        @ApiResponse(responseCode = "404", description = "Rapport non trouvé"),
                        @ApiResponse(responseCode = "400", description = "Analyse déjà effectuée")
        })
        public ResponseEntity<RapportDto> analyzeRapport(
                        @Parameter(description = "ID du rapport") @PathVariable Long id)
                        throws EntityNotFoundException, InvalidOperationException {
                log.info("POST /api/rapports/{}/analyze - Analyse du rapport", id);
                RapportDto analyzedRapport = rapportService.analyzeRapport(id);
                return ResponseEntity.ok(analyzedRapport);
        }

        @GetMapping("/recent")
        @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR')")
        @Operation(summary = "Récupérer les rapports récents", description = "Récupère les rapports récemment uploadés")
        @ApiResponse(responseCode = "200", description = "Succès")
        public ResponseEntity<Page<RapportDto>> getRecentRapports(
                        @PageableDefault(size = 10) Pageable pageable) {
                log.info("GET /api/rapports/recent - Récupération des rapports récents");
                Page<RapportDto> rapports = rapportService.getRecentRapports(pageable);
                return ResponseEntity.ok(rapports);
        }

        @GetMapping("/without-project")
        @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR')")
        @Operation(summary = "Récupérer les rapports sans projet", description = "Récupère les rapports non associés à un projet")
        @ApiResponse(responseCode = "200", description = "Succès")
        public ResponseEntity<List<RapportDto>> getRapportsWithoutProject() {
                log.info("GET /api/rapports/without-project - Récupération des rapports sans projet");
                List<RapportDto> rapports = rapportService.getRapportsWithoutProject();
                return ResponseEntity.ok(rapports);
        }

        @GetMapping("/statistics/budget")
        @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
        @Operation(summary = "Récupérer les statistiques de budget", description = "Récupère les budgets estimés par niveau de risque")
        @ApiResponse(responseCode = "200", description = "Succès")
        public ResponseEntity<Map<String, BigDecimal>> getBudgetEstimatesByRiskLevel() {
                log.info("GET /api/rapports/statistics/budget - Récupération des statistiques de budget");
                Map<String, BigDecimal> statistics = rapportService.getBudgetEstimatesByRiskLevel();
                return ResponseEntity.ok(statistics);
        }

        @GetMapping("/{id}/export-pdf")
        @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR')")
        @Operation(summary = "Exporter un rapport en PDF", description = "Exporte le rapport au format PDF")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "PDF généré avec succès"),
                        @ApiResponse(responseCode = "404", description = "Rapport non trouvé")
        })
        public ResponseEntity<Resource> exportRapportPDF(
                        @Parameter(description = "ID du rapport") @PathVariable Long id)
                        throws EntityNotFoundException, IOException {
                log.info("GET /api/rapports/{}/export-pdf - Export PDF du rapport", id);

                byte[] pdfContent = rapportService.generatePDFReport(id);
                ByteArrayResource resource = new ByteArrayResource(pdfContent);

                String filename = "rapport_" + id + "_" + System.currentTimeMillis() + ".pdf";

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                                .contentType(MediaType.APPLICATION_PDF)
                                .body(resource);
        }

        @PostMapping("/{id}/send-email")
        @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE')")
        @Operation(summary = "Envoyer un rapport par email", description = "Envoie le rapport en PDF par email")
        public ResponseEntity<Map<String, String>> sendRapportByEmail(
                        @Parameter(description = "ID du rapport") @PathVariable Long id,
                        @Parameter(description = "Adresse email destinataire") @RequestParam String email,
                        @Parameter(description = "Message personnel (optionnel)") @RequestParam(required = false) String message) {
                log.info("POST /api/rapports/{}/send-email - Envoi du rapport par email à {}", id, email);

                try {
                        rapportService.sendRapportByEmail(id, email, message);
                        return ResponseEntity.ok(Map.of(
                                        "message", "Rapport envoyé avec succès à " + email,
                                        "timestamp", String.valueOf(System.currentTimeMillis())));
                } catch (Exception e) {
                        log.error("Erreur d'envoi d'email pour le rapport {}: {}", id, e.getMessage());
                        return ResponseEntity.status(500).body(Map.of(
                                        "error", "Échec de l'envoi de l'email",
                                        "message",
                                        "Veuillez vérifier votre configuration SMTP dans application.properties."));
                }
        }
}
