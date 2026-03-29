package com.gs2e.stage_eranove_academy.phase.model;

public enum StatutPhase {
    A_VENIR("À Venir"),
    EN_COURS("En Cours"),
    TERMINEE("Terminée"),
    EN_RETARD("En Retard"),
    BLOQUEE("Bloquée");

    private final String libelle;

    StatutPhase(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
