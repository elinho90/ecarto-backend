package com.gs2e.stage_eranove_academy.alerte.model;

public enum NiveauAlerte {
    INFORMATION("Informatif"),
    AVERTISSEMENT("Avertissement"),
    IMPORTANT("Important"),
    URGENT("Urgent"),
    CRITIQUE("Critique");

    private final String libelle;

    NiveauAlerte(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
