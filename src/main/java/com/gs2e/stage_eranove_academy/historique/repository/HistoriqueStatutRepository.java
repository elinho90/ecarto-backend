package com.gs2e.stage_eranove_academy.historique.repository;

import com.gs2e.stage_eranove_academy.historique.model.HistoriqueStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueStatutRepository extends JpaRepository<HistoriqueStatut, Long> {
    List<HistoriqueStatut> findByProjetIdOrderByDateChangementDesc(Long projetId);
}
