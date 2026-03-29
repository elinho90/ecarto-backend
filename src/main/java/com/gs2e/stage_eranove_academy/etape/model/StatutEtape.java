package com.gs2e.stage_eranove_academy.etape.model;

public enum StatutEtape {
    A_FAIRE("À Faire"),
    EN_COURS("En Cours"),
    EN_ATTENTE_VALIDATION("En Attente de Validation"),
    VALIDEE("Validée"),
    REJETEE("Rejetée"),
    EN_RETARD("En Retard"),
    BLOQUEE("Bloquée");

    private final String libelle;

    StatutEtape(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
