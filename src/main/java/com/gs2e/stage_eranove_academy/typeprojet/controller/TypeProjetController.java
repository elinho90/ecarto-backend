package com.gs2e.stage_eranove_academy.typeprojet.controller;

import com.gs2e.stage_eranove_academy.typeprojet.dto.TypeProjetDto;
import com.gs2e.stage_eranove_academy.typeprojet.service.TypeProjetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/types-projet")
@Tag(name = "TypeProjet", description = "Endpoints pour la création et la consultation des typeProjets")
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET')")
@Slf4j
public class TypeProjetController {

    private final TypeProjetService typeProjetService;

    public TypeProjetController(TypeProjetService typeProjetService) {
        this.typeProjetService = typeProjetService;
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau typeprojet ")
    @ApiResponse(responseCode = "201", description = "typeprojet créée")
    public ResponseEntity<TypeProjetDto> create(@RequestBody TypeProjetDto typeProjetDto) {
        log.info("POST /api/types-projet - Création d'un nouveau type de projet");
        TypeProjetDto createdTypeProjetDto = typeProjetService.create(typeProjetDto);
        return new ResponseEntity<>(createdTypeProjetDto, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Reccuperer la liste des typeProjets par pagination")
    @ApiResponse(responseCode = "200", description = "Liste des typeProjets reccuperés")
    public ResponseEntity<Page<TypeProjetDto>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/types-projet - Récupération de tous les types de projet");
        Page<TypeProjetDto> pages = typeProjetService.getAll(pageable);
        return ResponseEntity.ok(pages);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Reccuperer un typeProjet par id")
    @ApiResponse(responseCode = "200", description = "TypeProjet reccuperé avec succès")
    public ResponseEntity<TypeProjetDto> getById(@PathVariable("id") Long id) {
        log.info("GET /api/types-projet/{} - Récupération du type de projet", id);
        TypeProjetDto foundTypeProjetDto = typeProjetService.getById(id);
        return ResponseEntity.ok(foundTypeProjetDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Suppression d'un typeProjet")
    @ApiResponse(responseCode = "200", description = "Suppression effectuée avec succès")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        log.info("DELETE /api/types-projet/{} - Suppression du type de projet", id);
        typeProjetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
