package com.gs2e.stage_eranove_academy.etape.model;

public enum TypeLivrable {
    DOCUMENT("Document"),
    CODE_SOURCE("Code Source"),
    MAQUETTE("Maquette / Prototype"),
    RAPPORT("Rapport"),
    PV_REUNION("PV de Réunion"),
    LIVRABLE_TECHNIQUE("Livrable Technique"),
    AUCUN("Aucun livrable");

    private final String libelle;

    TypeLivrable(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
