package com.gs2e.stage_eranove_academy.task.service;

import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.projet.model.Projet;
import com.gs2e.stage_eranove_academy.projet.repository.ProjetRepository;
import com.gs2e.stage_eranove_academy.task.dto.TaskDto;
import com.gs2e.stage_eranove_academy.task.dto.TaskReorderDto;
import com.gs2e.stage_eranove_academy.task.mapper.TaskMapper;
import com.gs2e.stage_eranove_academy.task.model.Task;
import com.gs2e.stage_eranove_academy.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjetRepository projetRepository;
    private final TaskMapper taskMapper;

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByProjet(Long projetId) {
        return taskRepository.findByProjetIdOrderByDisplayOrderAsc(projetId)
                .stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, List<TaskDto>> getTasksByProjetGroupedByStatus(Long projetId) {
        List<Task> tasks = taskRepository.findByProjetIdOrderByDisplayOrderAsc(projetId);

        Map<String, List<TaskDto>> grouped = new HashMap<>();
        grouped.put("TODO", tasks.stream()
                .filter(t -> t.getStatut() == Task.StatutTask.TODO)
                .map(taskMapper::toDto)
                .collect(Collectors.toList()));
        grouped.put("IN_PROGRESS", tasks.stream()
                .filter(t -> t.getStatut() == Task.StatutTask.IN_PROGRESS)
                .map(taskMapper::toDto)
                .collect(Collectors.toList()));
        grouped.put("DONE", tasks.stream()
                .filter(t -> t.getStatut() == Task.StatutTask.DONE)
                .map(taskMapper::toDto)
                .collect(Collectors.toList()));

        return grouped;
    }

    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tâche non trouvée avec l'ID: " + id));
        return taskMapper.toDto(task);
    }

    public TaskDto createTask(TaskDto dto) {
        Projet projet = projetRepository.findById(dto.getProjetId())
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + dto.getProjetId()));

        Task task = taskMapper.toEntity(dto);
        task.setProjet(projet);

        // Set display order to end of list for the status
        Integer maxOrder = taskRepository.findMaxDisplayOrderByProjetAndStatut(projet.getId(), task.getStatut());
        task.setDisplayOrder(maxOrder + 1);

        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    public TaskDto updateTask(Long id, TaskDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tâche non trouvée avec l'ID: " + id));

        taskMapper.updateEntity(task, dto);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    public TaskDto reorderTask(Long id, TaskReorderDto reorderDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tâche non trouvée avec l'ID: " + id));

        Task.StatutTask oldStatut = task.getStatut();
        Integer oldOrder = task.getDisplayOrder();
        Task.StatutTask newStatut = reorderDto.getNewStatut();
        Integer newOrder = reorderDto.getNewOrder();

        // If moving within the same column
        if (oldStatut == newStatut) {
            if (oldOrder < newOrder) {
                // Moving down - decrement orders between old and new
                taskRepository.decrementDisplayOrderFrom(task.getProjet().getId(), oldStatut, oldOrder);
                taskRepository.incrementDisplayOrderFrom(task.getProjet().getId(), newStatut, newOrder);
            } else if (oldOrder > newOrder) {
                // Moving up - increment orders between new and old
                taskRepository.incrementDisplayOrderFrom(task.getProjet().getId(), newStatut, newOrder);
            }
        } else {
            // Moving to different column
            // Decrement orders in old column
            taskRepository.decrementDisplayOrderFrom(task.getProjet().getId(), oldStatut, oldOrder);
            // Increment orders in new column
            taskRepository.incrementDisplayOrderFrom(task.getProjet().getId(), newStatut, newOrder);
        }

        task.setStatut(newStatut);
        task.setDisplayOrder(newOrder);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tâche non trouvée avec l'ID: " + id));

        // Decrement order of tasks after the deleted one
        taskRepository.decrementDisplayOrderFrom(task.getProjet().getId(), task.getStatut(), task.getDisplayOrder());

        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getTaskStatistics(Long projetId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", taskRepository.countByProjetId(projetId));
        stats.put("todo", taskRepository.countByProjetIdAndStatut(projetId, Task.StatutTask.TODO));
        stats.put("inProgress", taskRepository.countByProjetIdAndStatut(projetId, Task.StatutTask.IN_PROGRESS));
        stats.put("done", taskRepository.countByProjetIdAndStatut(projetId, Task.StatutTask.DONE));
        return stats;
    }
}
