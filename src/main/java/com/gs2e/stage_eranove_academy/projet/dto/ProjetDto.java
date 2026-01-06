package com.gs2e.stage_eranove_academy.projet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gs2e.stage_eranove_academy.projet.model.Projet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjetDto {

    private Long id;

    @NotBlank(message = "Le nom du projet est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String nom;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @NotNull(message = "Le statut du projet est obligatoire")
    private Projet.StatutProjet statut;

    @NotNull(message = "La priorité du projet est obligatoire")
    private Projet.PrioriteProjet priorite;

    @NotBlank(message = "Le responsable du projet est obligatoire")
    private String responsable;

    @NotNull(message = "La date de début est obligatoire")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFinPrevue;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFinReelle;

    @DecimalMin(value = "0.0", inclusive = false, message = "Le budget doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2, message = "Le budget doit avoir au maximum 10 chiffres avant la virgule et 2 après")
    private BigDecimal budget;

    @Min(value = 0, message = "La progression ne peut pas être négative")
    @Max(value = 100, message = "La progression ne peut pas dépasser 100")
    private Integer progression = 0;

    private Long typeProjetId;
    private String typeProjetNom;
    private Long siteId;
    private String siteNom;
    private Set<String> equipe;
    private String tags;

    // Champs calculés
    private Long dureeJours;
    private BigDecimal coutParJour;
}