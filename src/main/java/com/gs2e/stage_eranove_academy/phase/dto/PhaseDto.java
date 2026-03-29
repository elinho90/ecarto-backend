package com.gs2e.stage_eranove_academy.phase.dto;

import com.gs2e.stage_eranove_academy.etape.dto.EtapeDto;
import com.gs2e.stage_eranove_academy.phase.model.StatutPhase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhaseDto {
    private Long id;

    @NotBlank(message = "Le nom de la phase est obligatoire")
    private String nom;

    private String description;
    private Integer ordre;
    private Long projetId;
    private String projetNom;
    private LocalDate dateDebutPrevue;
    private LocalDate dateFinPrevue;
    private LocalDate dateDebutReelle;
    private LocalDate dateFinReelle;

    @Min(0) @Max(100)
    private Integer progression = 0;

    private StatutPhase statut;
    private Boolean verrouillee;

    // Étapes de cette phase
    private List<EtapeDto> etapes;

    // Champs calculés
    private Integer totalEtapes;
    private Integer etapesValidees;
    private Integer etapesEnRetard;
}
