package com.gs2e.stage_eranove_academy.security.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "permissions")
@Data
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String role;  // Ex: "ADMINISTRATEUR_SYSTEME"

    @Column(nullable = false)
    private String resource;  // Ex: "PROJET", "UTILISATEUR", "LOG"

    @Column(nullable = false)
    private String action;  // Ex: "CREER", "VOIR", "MODIFIER", "SUPPRIMER"
}