package com.gs2e.stage_eranove_academy.security.controller;

import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import com.gs2e.stage_eranove_academy.security.repository.UtilisateurRepository;
import com.gs2e.stage_eranove_academy.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Profil", description = "Gestion du profil utilisateur connecté")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final AuthService authService;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping
    @Operation(summary = "Récupérer le profil connecté")
    public ResponseEntity<Utilisateur> getCurrentProfile() {
        log.info("GET /api/profile - Récupération du profil connecté");
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PatchMapping("/preferences")
    @Operation(summary = "Mettre à jour les préférences du tableau de bord")
    public ResponseEntity<Utilisateur> updateDashboardPreferences(@RequestBody Map<String, String> preferences) {
        log.info("PATCH /api/profile/preferences - Mise à jour des préférences");

        Utilisateur currentUser = authService.getCurrentUser();
        String config = preferences.get("dashboardConfig");

        if (config != null) {
            currentUser.setDashboardConfig(config);
            utilisateurRepository.save(currentUser);
            log.info("Préférences mises à jour pour l'utilisateur: {}", currentUser.getEmail());
        }

        return ResponseEntity.ok(currentUser);
    }
}
