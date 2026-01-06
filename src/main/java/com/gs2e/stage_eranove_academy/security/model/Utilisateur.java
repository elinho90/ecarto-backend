package com.gs2e.stage_eranove_academy.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false, length = 254)
    private String password;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role = Role.OBSERVATEUR;

    @Column(length = 20)
    private String telephone;

    @Column(length = 100)
    private String departement;

    @Column(length = 100)
    private String poste;

    @Column(nullable = false)
    private Boolean actif = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "refresh_token_expiry")
    private LocalDateTime refreshTokenExpiry;

    public enum Role {
        ADMINISTRATEUR_SYSTEME,  // Administrateur système
        CHEF_DE_PROJET,          // Chef de projet
        ANALYSTE,                // Analyste / Chargé d'étude
        DEVELOPPEUR,             // Développeur / Équipe technique
        DECIDEUR,                // Décideur / Direction
        OBSERVATEUR              // Utilisateur simple / Observateur
    }
}