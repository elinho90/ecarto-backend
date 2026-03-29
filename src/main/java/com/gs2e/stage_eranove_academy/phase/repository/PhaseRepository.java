package com.gs2e.stage_eranove_academy.phase.repository;

import com.gs2e.stage_eranove_academy.phase.model.Phase;
import com.gs2e.stage_eranove_academy.phase.model.StatutPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, Long> {
    List<Phase> findByProjetIdOrderByOrdre(Long projetId);
    List<Phase> findByProjetIdAndStatut(Long projetId, StatutPhase statut);
    long countByProjetId(Long projetId);
}
