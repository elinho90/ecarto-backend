package com.gs2e.stage_eranove_academy.validation.model;

public enum DecisionValidation {
    APPROUVEE("Approuvée"),
    REJETEE("Rejetée"),
    DEMANDE_MODIFICATION("Modification Demandée");

    private final String libelle;

    DecisionValidation(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
