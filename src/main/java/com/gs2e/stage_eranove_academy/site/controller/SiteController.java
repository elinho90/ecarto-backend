package com.gs2e.stage_eranove_academy.site.controller;

import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.common.Exceptions.InvalidOperationException;
import com.gs2e.stage_eranove_academy.site.dto.SiteDto;
import com.gs2e.stage_eranove_academy.site.model.Site;
import com.gs2e.stage_eranove_academy.site.service.SiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sites")
@Tag(name = "Sites", description = "API de gestion des sites géographiques")
@Slf4j
public class SiteController {

    private final SiteService siteService;

    @Autowired
    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer tous les sites", description = "Récupère la liste paginée de tous les sites")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<Page<SiteDto>> getAllSites(
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("GET /api/sites - Récupération de tous les sites");
        Page<SiteDto> sites = siteService.getAllSites(pageable);
        return ResponseEntity.ok(sites);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer un site par ID", description = "Récupère un site spécifique par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Site trouvé"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé")
    })
    public ResponseEntity<SiteDto> getSiteById(
            @Parameter(description = "ID du site") @PathVariable Long id) throws EntityNotFoundException {
        log.info("GET /api/sites/{} - Récupération du site", id);
        SiteDto site = siteService.getSiteById(id);
        return ResponseEntity.ok(site);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET')")
    @Operation(summary = "Créer un nouveau site", description = "Crée un nouveau site géographique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Site créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<SiteDto> createSite(
            @Valid @RequestBody SiteDto siteDto) throws InvalidOperationException {
        log.info("POST /api/sites - Création d'un nouveau site");
        SiteDto createdSite = siteService.createSite(siteDto);
        return new ResponseEntity<>(createdSite, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET')")
    @Operation(summary = "Mettre à jour un site", description = "Met à jour un site existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Site mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé")
    })
    public ResponseEntity<SiteDto> updateSite(
            @Parameter(description = "ID du site") @PathVariable Long id,
            @Valid @RequestBody SiteDto siteDto) throws EntityNotFoundException, InvalidOperationException {
        log.info("PUT /api/sites/{} - Mise à jour du site", id);
        SiteDto updatedSite = siteService.updateSite(id, siteDto);
        return ResponseEntity.ok(updatedSite);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_SYSTEME')")
    @Operation(summary = "Supprimer un site", description = "Supprime un site existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Site supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé")
    })
    public ResponseEntity<Void> deleteSite(
            @Parameter(description = "ID du site") @PathVariable Long id) throws EntityNotFoundException {
        log.info("DELETE /api/sites/{} - Suppression du site", id);
        siteService.deleteSite(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Rechercher des sites", description = "Recherche multicritères de sites")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<Page<SiteDto>> searchSites(
            @Parameter(description = "Nom du site") @RequestParam(required = false) String nom,
            @Parameter(description = "Ville") @RequestParam(required = false) String ville,
            @Parameter(description = "Région") @RequestParam(required = false) String region,
            @Parameter(description = "Type de site") @RequestParam(required = false) Site.TypeSite type,
            @Parameter(description = "Statut") @RequestParam(required = false) Site.StatutSite statut,
            @PageableDefault(size = 10) Pageable pageable) {

        log.info("GET /api/sites/search - Recherche de sites avec critères");
        Page<SiteDto> sites = siteService.searchSites(nom, ville, region, type, statut, pageable);
        return ResponseEntity.ok(sites);
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer des sites par type", description = "Récupère tous les sites d'un type spécifique")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<Page<SiteDto>> getSitesByType(
            @Parameter(description = "Type de site") @PathVariable Site.TypeSite type,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("GET /api/sites/type/{} - Récupération des sites par type", type);
        Page<SiteDto> sites = siteService.getSitesByType(type, pageable);
        return ResponseEntity.ok(sites);
    }

    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer des sites par statut", description = "Récupère tous les sites avec un statut spécifique")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<Page<SiteDto>> getSitesByStatut(
            @Parameter(description = "Statut du site") @PathVariable Site.StatutSite statut,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("GET /api/sites/statut/{} - Récupération des sites par statut", statut);
        Page<SiteDto> sites = siteService.getSitesByStatut(statut, pageable);
        return ResponseEntity.ok(sites);
    }

    @GetMapping("/bounds")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer des sites dans une zone géographique", description = "Récupère les sites dans les limites géographiques données")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<List<SiteDto>> getSitesInBounds(
            @Parameter(description = "Latitude minimum") @RequestParam Double latMin,
            @Parameter(description = "Latitude maximum") @RequestParam Double latMax,
            @Parameter(description = "Longitude minimum") @RequestParam Double lngMin,
            @Parameter(description = "Longitude maximum") @RequestParam Double lngMax) {
        log.info("GET /api/sites/bounds - Récupération des sites dans les limites géographiques");
        List<SiteDto> sites = siteService.getSitesInBounds(latMin, latMax, lngMin, lngMax);
        return ResponseEntity.ok(sites);
    }

    @GetMapping("/regions")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer toutes les régions", description = "Récupère la liste de toutes les régions disponibles")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<List<String>> getAllRegions() {
        log.info("GET /api/sites/regions - Récupération de toutes les régions");
        List<String> regions = siteService.getAllRegions();
        return ResponseEntity.ok(regions);
    }

    @GetMapping("/regions/{region}/villes")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer les villes par région", description = "Récupère la liste des villes pour une région donnée")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<List<String>> getVillesByRegion(
            @Parameter(description = "Nom de la région") @PathVariable String region) {
        log.info("GET /api/sites/regions/{}/villes - Récupération des villes par région", region);
        List<String> villes = siteService.getVillesByRegion(region);
        return ResponseEntity.ok(villes);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'DECIDEUR')")
    @Operation(summary = "Récupérer les statistiques", description = "Récupère les statistiques globales des sites")
    @ApiResponse(responseCode = "200", description = "Succès")
    public ResponseEntity<Map<String, Object>> getSiteStatistics() {
        log.info("GET /api/sites/statistics - Récupération des statistiques");
        Map<String, Object> statistics = siteService.getSiteStatistics();
        return ResponseEntity.ok(statistics);
    }
}
