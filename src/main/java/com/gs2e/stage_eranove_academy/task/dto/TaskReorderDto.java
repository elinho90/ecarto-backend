package com.gs2e.stage_eranove_academy.task.dto;

import com.gs2e.stage_eranove_academy.task.model.Task;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskReorderDto {

    @NotNull(message = "Le nouveau statut est obligatoire")
    private Task.StatutTask newStatut;

    @NotNull(message = "La nouvelle position est obligatoire")
    private Integer newOrder;
}
