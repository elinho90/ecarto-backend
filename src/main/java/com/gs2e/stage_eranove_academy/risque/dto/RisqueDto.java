package com.gs2e.stage_eranove_academy.risque.dto;

import com.gs2e.stage_eranove_academy.risque.model.NiveauRisque;
import com.gs2e.stage_eranove_academy.risque.model.StatutRisque;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RisqueDto {
    private Long id;

    @NotNull(message = "Le projet est obligatoire")
    private Long projetId;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    @NotNull(message = "La probabilité est obligatoire")
    private NiveauRisque probabilite;

    @NotNull(message = "L'impact est obligatoire")
    private NiveauRisque impact;

    private String planMitigation;
    
    private StatutRisque statut;
    
    private Long responsableId;
    private String responsableNom; // Prénom + Nom

    private LocalDate dateIdentification;
    private LocalDate dateResolution;
}
