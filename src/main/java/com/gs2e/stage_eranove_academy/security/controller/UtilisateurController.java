package com.gs2e.stage_eranove_academy.security.controller;

import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import com.gs2e.stage_eranove_academy.security.repository.UtilisateurRepository;
import com.gs2e.stage_eranove_academy.security.service.AuthService;
import com.gs2e.stage_eranove_academy.security.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@Tag(name = "Utilisateurs", description = "API de gestion des utilisateurs")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMINISTRATEUR_SYSTEME')")
public class UtilisateurController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final AuthService authService;

    @GetMapping
    @Operation(summary = "Récupérer tous les utilisateurs")
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        log.info("GET /api/utilisateurs - Récupération de tous les utilisateurs");
        return ResponseEntity.ok(utilisateurRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur par ID")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Long id) {
        log.info("GET /api/utilisateurs/{} - Récupération de l'utilisateur", id);
        return utilisateurRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un nouvel utilisateur")
    public ResponseEntity<Utilisateur> createUtilisateur(@RequestBody Utilisateur utilisateur) {
        log.info("POST /api/utilisateurs - Création d'un nouvel utilisateur: {}", utilisateur.getEmail());

        // Vérifier si l'email existe déjà
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
            log.warn("Email déjà utilisé: {}", utilisateur.getEmail());
            return ResponseEntity.badRequest().build();
        }

        // Encoder le mot de passe
        if (utilisateur.getPassword() != null && !utilisateur.getPassword().isEmpty()) {
            utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        }

        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
        log.info("Utilisateur créé avec succès: ID={}", savedUtilisateur.getId());

        // Notification
        try {
            Utilisateur currentUser = authService.getCurrentUser();
            notificationService.createNotification(
                    currentUser,
                    "USER_CREATION",
                    "Nouvel utilisateur créé",
                    "L'utilisateur '" + savedUtilisateur.getPrenom() + " " + savedUtilisateur.getNom()
                            + "' a été créé.");
        } catch (Exception e) {
            log.warn("Impossible de créer la notification pour l'utilisateur: {}", e.getMessage());
        }

        return ResponseEntity.ok(savedUtilisateur);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un utilisateur")
    public ResponseEntity<Utilisateur> updateUtilisateur(@PathVariable Long id, @RequestBody Utilisateur utilisateur) {
        log.info("PUT /api/utilisateurs/{} - Mise à jour de l'utilisateur", id);

        return utilisateurRepository.findById(id)
                .map(existingUser -> {
                    // Mettre à jour les champs
                    existingUser.setNom(utilisateur.getNom());
                    existingUser.setPrenom(utilisateur.getPrenom());
                    existingUser.setEmail(utilisateur.getEmail());
                    existingUser.setRole(utilisateur.getRole());
                    existingUser.setTelephone(utilisateur.getTelephone());
                    existingUser.setDepartement(utilisateur.getDepartement());
                    existingUser.setPoste(utilisateur.getPoste());
                    existingUser.setActif(utilisateur.getActif());

                    // Mettre à jour le mot de passe seulement s'il est fourni
                    if (utilisateur.getPassword() != null && !utilisateur.getPassword().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
                    }

                    Utilisateur updatedUser = utilisateurRepository.save(existingUser);
                    log.info("Utilisateur mis à jour avec succès: ID={}", updatedUser.getId());

                    // Notification
                    try {
                        Utilisateur currentUser = authService.getCurrentUser();
                        notificationService.createNotification(
                                currentUser,
                                "USER_UPDATE",
                                "Utilisateur mis à jour",
                                "L'utilisateur '" + updatedUser.getPrenom() + " " + updatedUser.getNom()
                                        + "' a été mis à jour.");
                    } catch (Exception e) {
                        log.warn("Impossible de créer la notification pour l'utilisateur: {}", e.getMessage());
                    }

                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        log.info("DELETE /api/utilisateurs/{} - Suppression de l'utilisateur", id);
        Utilisateur user = utilisateurRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        String userLabel = user.getPrenom() + " " + user.getNom();
        utilisateurRepository.deleteById(id);
        log.info("Utilisateur supprimé avec succès: ID={}", id);

        // Notification
        try {
            Utilisateur currentUser = authService.getCurrentUser();
            notificationService.createNotification(
                    currentUser,
                    "USER_DELETION",
                    "Utilisateur supprimé",
                    "L'utilisateur '" + userLabel + "' a été supprimé.");
        } catch (Exception e) {
            log.warn("Impossible de créer la notification pour l'utilisateur: {}", e.getMessage());
        }

        return ResponseEntity.noContent().build();
    }
}
