package com.gs2e.stage_eranove_academy.validation.dto;

import com.gs2e.stage_eranove_academy.validation.model.DecisionValidation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationEtapeDto {
    private Long id;
    private Long etapeId;
    private String etapeNom;
    private Long validateurId;
    private String validateurNom;

    @NotNull(message = "La décision de validation est obligatoire")
    private DecisionValidation decision;

    private String commentaire;
    private LocalDateTime dateValidation;
    private String piecesJointes;
}
