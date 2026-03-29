package com.gs2e.stage_eranove_academy.task.dto;

import com.gs2e.stage_eranove_academy.task.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String title;

    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    private String description;

    @NotNull(message = "Le statut est obligatoire")
    private Task.StatutTask statut;

    private Integer displayOrder;

    @NotNull(message = "L'ID du projet est obligatoire")
    private Long projetId;

    private String projetNom;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
