package com.gs2e.stage_eranove_academy.task.mapper;

import com.gs2e.stage_eranove_academy.task.dto.TaskDto;
import com.gs2e.stage_eranove_academy.task.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskDto toDto(Task task) {
        if (task == null) {
            return null;
        }

        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .statut(task.getStatut())
                .displayOrder(task.getDisplayOrder())
                .projetId(task.getProjet() != null ? task.getProjet().getId() : null)
                .projetNom(task.getProjet() != null ? task.getProjet().getNom() : null)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public Task toEntity(TaskDto dto) {
        if (dto == null) {
            return null;
        }

        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatut(dto.getStatut() != null ? dto.getStatut() : Task.StatutTask.TODO);
        task.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        return task;
    }

    public void updateEntity(Task task, TaskDto dto) {
        if (dto.getTitle() != null) {
            task.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }
        if (dto.getStatut() != null) {
            task.setStatut(dto.getStatut());
        }
        if (dto.getDisplayOrder() != null) {
            task.setDisplayOrder(dto.getDisplayOrder());
        }
    }
}
