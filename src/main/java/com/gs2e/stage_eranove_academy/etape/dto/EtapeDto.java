package com.gs2e.stage_eranove_academy.etape.dto;

import com.gs2e.stage_eranove_academy.etape.model.StatutEtape;
import com.gs2e.stage_eranove_academy.etape.model.TypeLivrable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EtapeDto {
    private Long id;

    @NotBlank(message = "Le nom de l'étape est obligatoire")
    private String nom;

    private String description;
    private Integer ordre;
    private Long phaseId;
    private String phaseNom;
    private Long responsableId;
    private String responsableNom;

    @NotNull(message = "La date d'échéance est obligatoire")
    private LocalDate dateEcheance;

    private LocalDate dateRealisation;
    private Integer dureeEstimeeJours;
    private Integer dureeReelleJours;
    private StatutEtape statut;
    private Boolean validationRequise;
    private Boolean bloquante;
    private TypeLivrable typeLivrable;
    private String urlLivrable;

    // Champs calculés
    private Boolean enRetard;
    private Long joursRetard;
    private Long joursRestants;
}
