package com.gs2e.stage_eranove_academy.alerte.repository;

import com.gs2e.stage_eranove_academy.alerte.model.Alerte;
import com.gs2e.stage_eranove_academy.alerte.model.NiveauAlerte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Long> {
    List<Alerte> findByProjetIdOrderByCreatedAtDesc(Long projetId);
    List<Alerte> findByDestinataire_IdAndLueFalseOrderByCreatedAtDesc(Long destinataireId);
    List<Alerte> findByProjetIdAndResolueFalseOrderByCreatedAtDesc(Long projetId);
    long countByDestinataire_IdAndLueFalse(Long destinataireId);
    List<Alerte> findByNiveauAndResolueFalse(NiveauAlerte niveau);
}
