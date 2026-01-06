package com.gs2e.stage_eranove_academy.site.dto;

import com.gs2e.stage_eranove_academy.site.model.Site;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteDto {

    private Long id;

    @NotBlank(message = "Le nom du site est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String nom;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @NotBlank(message = "La ville est obligatoire")
    private String ville;

    @NotBlank(message = "La région est obligatoire")
    private String region;

    private String pays = "Côte d'Ivoire";

    @NotNull(message = "La latitude est obligatoire")
    @DecimalMin(value = "-90.0", message = "La latitude doit être entre -90 et 90")
    @DecimalMax(value = "90.0", message = "La latitude doit être entre -90 et 90")
    private Double latitude;

    @NotNull(message = "La longitude est obligatoire")
    @DecimalMin(value = "-180.0", message = "La longitude doit être entre -180 et 180")
    @DecimalMax(value = "180.0", message = "La longitude doit être entre -180 et 180")
    private Double longitude;

    @NotNull(message = "Le type de site est obligatoire")
    private Site.TypeSite type;

    private Site.StatutSite statut = Site.StatutSite.ACTIF;

    private String contactPersonne;

    @Pattern(regexp = "^\\+?[0-9\\s-()]+$", message = "Le numéro de téléphone n'est pas valide")
    private String contactTelephone;

    @Email(message = "L'email de contact doit être valide")
    private String contactEmail;

    @Min(value = 1, message = "Le nombre d'employés doit être positif")
    private Integer nombreEmployes;

    private String horairesOuverture;

    private String equipements;
}