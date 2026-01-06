package com.gs2e.stage_eranove_academy.rapport.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gs2e.stage_eranove_academy.rapport.model.Rapport;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RapportDto {

    @Null(message = "L'ID doit être nul lors de la création")
    private Long id;

    @NotBlank(message = "Le nom du rapport est obligatoire")
    @Size(min = 3, max = 200, message = "Le nom doit contenir entre 3 et 200 caractères")
    private String nom;

    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    private String description;

    @NotBlank(message = "Le nom du fichier est obligatoire")
    private String fichierNom;

    @NotBlank(message = "Le type du fichier est obligatoire")
    @Pattern(regexp = "^(pdf|doc|docx)$", message = "Le type de fichier doit être pdf, doc ou docx")
    private String fichierType;

    @NotNull(message = "La taille du fichier est obligatoire")
    @Min(value = 1, message = "La taille du fichier doit être positive")
    @Max(value = 10485760, message = "La taille du fichier ne peut pas dépasser 10MB")
    private Long fichierTaille;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Sécurité: n'apparaît pas dans les réponses
    private String fichierChemin;

    private Long projetId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String projetNom;

    @NotBlank(message = "L'auteur de l'upload est obligatoire")
    private String uploadePar;

    @NotNull(message = "Le taux de faisabilité est obligatoire")
    @Min(value = 0, message = "Le taux de faisabilité ne peut pas être négatif")
    @Max(value = 100, message = "Le taux de faisabilité ne peut pas dépasser 100")
    private Integer faisabilite;

    @NotNull(message = "Le niveau de risque est obligatoire")
    private Rapport.NiveauRisque risque;

    @DecimalMin(value = "0.0", inclusive = false, message = "Le budget estimé doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2, message = "Le budget estimé doit avoir au maximum 10 chiffres avant la virgule et 2 après")
    private BigDecimal budgetEstime;

    @NotNull(message = "La durée estimée est obligatoire")
    @Min(value = 1, message = "La durée estimée doit être d'au moins 1 mois")
    @Max(value = 120, message = "La durée estimée ne peut pas dépasser 120 mois")
    private Integer dureeEstimeeMois;

    @Size(max = 5000, message = "Les recommandations ne peuvent pas dépasser 5000 caractères")
    private String recommandations;

    private Boolean analyseAutomatique;

    // Champ pour la gestion de la concurrence
    private Long version;

    // Champs calculés (lecture seule)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String fichierTailleFormatee;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String dureeEstimeeFormatee;
}