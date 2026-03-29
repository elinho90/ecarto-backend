package com.gs2e.stage_eranove_academy.typeprojet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeProjetDto {

    private Long id;

    @NotBlank(message = "Le nom du type est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    @Size(max = 100, message = "Le libellé ne peut pas dépasser 100 caractères")
    private String libelle;

    @Size(max = 20, message = "La couleur ne peut pas dépasser 20 caractères")
    private String couleur;

    @Size(max = 50, message = "L'icône ne peut pas dépasser 50 caractères")
    private String icone;

    @Builder.Default
    private Boolean estActif = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
