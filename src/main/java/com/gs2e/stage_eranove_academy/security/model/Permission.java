package com.gs2e.stage_eranove_academy.security.model;

import lombok.Data;

/**
 * DÉSACTIVÉ : Cette classe n'est plus une entité JPA.
 * La table 'permissions' a été supprimée (V13) car elle n'était pas utilisée.
 * La gestion des droits se fait via l'enum Role dans Utilisateur.java.
 * Cette classe est conservée comme POJO pour référence future.
 */
@Data
public class Permission {

    private Long id;

    private String role; // Ex: "ADMINISTRATEUR_SYSTEME"
    private String resource; // Ex: "PROJET", "UTILISATEUR", "LOG"
    private String action; // Ex: "CREER", "VOIR", "MODIFIER", "SUPPRIMER"
}