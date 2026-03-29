package com.gs2e.stage_eranove_academy.validation.controller;

import com.gs2e.stage_eranove_academy.validation.dto.ValidationEtapeDto;
import com.gs2e.stage_eranove_academy.validation.service.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/validations")
@Tag(name = "Validations d'étapes", description = "API de consultation de l'historique des validations")
@Slf4j
@RequiredArgsConstructor
public class ValidationController {

    private final ValidationService validationService;

    @GetMapping("/etape/{etapeId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Lister l'historique des validations pour une étape")
    public ResponseEntity<List<ValidationEtapeDto>> getValidationsByEtape(@PathVariable Long etapeId) {
        log.info("GET /api/validations/etape/{}", etapeId);
        return ResponseEntity.ok(validationService.getValidationsByEtape(etapeId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'CHEF_DE_PROJET', 'ANALYSTE', 'DEVELOPPEUR', 'DECIDEUR', 'OBSERVATEUR')")
    @Operation(summary = "Récupérer une validation par son ID")
    public ResponseEntity<ValidationEtapeDto> getValidationById(@PathVariable Long id) {
        log.info("GET /api/validations/{}", id);
        return ResponseEntity.ok(validationService.getValidationById(id));
    }
}

