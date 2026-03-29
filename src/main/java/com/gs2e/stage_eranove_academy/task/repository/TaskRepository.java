package com.gs2e.stage_eranove_academy.task.repository;

import com.gs2e.stage_eranove_academy.task.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjetIdOrderByDisplayOrderAsc(Long projetId);

    List<Task> findByProjetIdAndStatutOrderByDisplayOrderAsc(Long projetId, Task.StatutTask statut);

    @Query("SELECT COALESCE(MAX(t.displayOrder), 0) FROM Task t WHERE t.projet.id = :projetId AND t.statut = :statut")
    Integer findMaxDisplayOrderByProjetAndStatut(@Param("projetId") Long projetId,
            @Param("statut") Task.StatutTask statut);

    @Modifying
    @Query("UPDATE Task t SET t.displayOrder = t.displayOrder + 1 WHERE t.projet.id = :projetId AND t.statut = :statut AND t.displayOrder >= :fromOrder")
    void incrementDisplayOrderFrom(@Param("projetId") Long projetId, @Param("statut") Task.StatutTask statut,
            @Param("fromOrder") Integer fromOrder);

    @Modifying
    @Query("UPDATE Task t SET t.displayOrder = t.displayOrder - 1 WHERE t.projet.id = :projetId AND t.statut = :statut AND t.displayOrder > :fromOrder")
    void decrementDisplayOrderFrom(@Param("projetId") Long projetId, @Param("statut") Task.StatutTask statut,
            @Param("fromOrder") Integer fromOrder);

    Long countByProjetId(Long projetId);

    Long countByProjetIdAndStatut(Long projetId, Task.StatutTask statut);
}
