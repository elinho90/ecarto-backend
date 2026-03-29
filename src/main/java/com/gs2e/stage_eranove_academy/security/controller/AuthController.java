package com.gs2e.stage_eranove_academy.security.controller;

import com.gs2e.stage_eranove_academy.security.dto.LoginRequest;
import com.gs2e.stage_eranove_academy.security.dto.LoginResponse;
import com.gs2e.stage_eranove_academy.security.dto.RegisterRequest;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import com.gs2e.stage_eranove_academy.security.service.AuthService;  // ✅ IMPORT AJOUTÉ
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentification", description = "API d'authentification")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Tentative de connexion: {}", loginRequest.getEmail());
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            log.warn("Échec d'authentification: {}", e.getReason());
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription utilisateur")
    public ResponseEntity<Utilisateur> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Création utilisateur: {}", registerRequest.getEmail());
        try {
            Utilisateur utilisateur = authService.register(registerRequest);
            return new ResponseEntity<>(utilisateur, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            log.error("Erreur création utilisateur: {}", e.getReason());
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion")
    public ResponseEntity<Void> logout() {
        log.info("Déconnexion utilisateur");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Profil utilisateur")
    public ResponseEntity<Utilisateur> getCurrentUser() {
        log.info("Récupération profil utilisateur");
        try {
            Utilisateur utilisateur = authService.getCurrentUser();
            return ResponseEntity.ok(utilisateur);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}
