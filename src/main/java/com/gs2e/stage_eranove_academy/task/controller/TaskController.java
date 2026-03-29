package com.gs2e.stage_eranove_academy.task.controller;

import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.task.dto.TaskDto;
import com.gs2e.stage_eranove_academy.task.dto.TaskReorderDto;
import com.gs2e.stage_eranove_academy.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "API de gestion des tâches Kanban")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/projets/{projetId}/tasks")
    @Operation(summary = "Récupérer toutes les tâches d'un projet")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskDto>> getTasksByProjet(
            @Parameter(description = "ID du projet") @PathVariable Long projetId) {
        return ResponseEntity.ok(taskService.getTasksByProjet(projetId));
    }

    @GetMapping("/projets/{projetId}/tasks/grouped")
    @Operation(summary = "Récupérer les tâches d'un projet groupées par statut")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, List<TaskDto>>> getTasksGroupedByStatus(
            @Parameter(description = "ID du projet") @PathVariable Long projetId) {
        return ResponseEntity.ok(taskService.getTasksByProjetGroupedByStatus(projetId));
    }

    @GetMapping("/projets/{projetId}/tasks/statistics")
    @Operation(summary = "Récupérer les statistiques des tâches d'un projet")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> getTaskStatistics(
            @Parameter(description = "ID du projet") @PathVariable Long projetId) {
        return ResponseEntity.ok(taskService.getTaskStatistics(projetId));
    }

    @PostMapping("/projets/{projetId}/tasks")
    @Operation(summary = "Créer une nouvelle tâche")
    @PreAuthorize("hasAnyRole('ADMIN', 'INGENIEUR')")
    public ResponseEntity<TaskDto> createTask(
            @Parameter(description = "ID du projet") @PathVariable Long projetId,
            @Valid @RequestBody TaskDto taskDto) {
        taskDto.setProjetId(projetId);
        TaskDto created = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/tasks/{id}")
    @Operation(summary = "Mettre à jour une tâche")
    @PreAuthorize("hasAnyRole('ADMIN', 'INGENIEUR')")
    public ResponseEntity<TaskDto> updateTask(
            @Parameter(description = "ID de la tâche") @PathVariable Long id,
            @Valid @RequestBody TaskDto taskDto) {
        try {
            TaskDto updated = taskService.updateTask(id, taskDto);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/tasks/{id}/reorder")
    @Operation(summary = "Réordonner une tâche (drag & drop)")
    @PreAuthorize("hasAnyRole('ADMIN', 'INGENIEUR')")
    public ResponseEntity<TaskDto> reorderTask(
            @Parameter(description = "ID de la tâche") @PathVariable Long id,
            @Valid @RequestBody TaskReorderDto reorderDto) {
        try {
            TaskDto reordered = taskService.reorderTask(id, reorderDto);
            return ResponseEntity.ok(reordered);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/tasks/{id}")
    @Operation(summary = "Supprimer une tâche")
    @PreAuthorize("hasAnyRole('ADMIN', 'INGENIEUR')")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID de la tâche") @PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
