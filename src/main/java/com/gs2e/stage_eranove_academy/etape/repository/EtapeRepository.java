package com.gs2e.stage_eranove_academy.etape.repository;

import com.gs2e.stage_eranove_academy.etape.model.Etape;
import com.gs2e.stage_eranove_academy.etape.model.StatutEtape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EtapeRepository extends JpaRepository<Etape, Long> {

    List<Etape> findByPhaseIdOrderByOrdre(Long phaseId);

    List<Etape> findByPhaseIdAndStatut(Long phaseId, StatutEtape statut);

    List<Etape> findByResponsableId(Long responsableId);

    // Étapes en retard (non validées et deadline dépassée)
    @Query("SELECT e FROM Etape e WHERE e.statut NOT IN (:excludedStatuts) AND e.dateEcheance < :today")
    List<Etape> findEtapesEnRetard(
            @Param("excludedStatuts") List<StatutEtape> excludedStatuts,
            @Param("today") LocalDate today);

    // Étapes bloquantes d'un projet
    @Query("SELECT e FROM Etape e WHERE e.phase.projet.id = :projetId AND e.bloquante = true")
    List<Etape> findByPhaseProjetIdAndBloquanteTrue(@Param("projetId") Long projetId);

    // Étapes proches de l'échéance
    @Query("SELECT e FROM Etape e WHERE e.statut NOT IN (:excludedStatuts) AND e.dateEcheance BETWEEN :today AND :limit")
    List<Etape> findEtapesProchesEcheance(
            @Param("excludedStatuts") List<StatutEtape> excludedStatuts,
            @Param("today") LocalDate today,
            @Param("limit") LocalDate limit);

    // Compter les étapes par statut pour une phase
    @Query("SELECT e.statut, COUNT(e) FROM Etape e WHERE e.phase.id = :phaseId GROUP BY e.statut")
    List<Object[]> countByStatutForPhase(@Param("phaseId") Long phaseId);

    // Étapes à valider (soumises et en attente)
    List<Etape> findByStatut(StatutEtape statut);
}
