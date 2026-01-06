package com.gs2e.stage_eranove_academy.projet.repository;

import com.gs2e.stage_eranove_academy.projet.model.Projet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {

       // Recherche par nom contenant
       Page<Projet> findByNomContainingIgnoreCase(String nom, Pageable pageable);

       // Recherche par statut
       Page<Projet> findByStatut(Projet.StatutProjet statut, Pageable pageable);

       // Recherche par responsable
       Page<Projet> findByResponsableContainingIgnoreCase(String responsable, Pageable pageable);

       // Recherche par type de projet
       Page<Projet> findByTypeProjetId(Long typeProjetId, Pageable pageable);

       // Recherche multicritères
       @Query("SELECT p FROM Projet p WHERE " +
                     "(:nom IS NULL OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
                     "(:statut IS NULL OR p.statut = :statut) AND " +
                     "(:responsable IS NULL OR LOWER(p.responsable) LIKE LOWER(CONCAT('%', :responsable, '%'))) AND " +
                     "(:typeProjetId IS NULL OR p.typeProjet.id = :typeProjetId) AND " +
                     "(:dateDebutFrom IS NULL OR p.dateDebut >= :dateDebutFrom) AND " +
                     "(:dateDebutTo IS NULL OR p.dateDebut <= :dateDebutTo) AND " +
                     "(:budgetMin IS NULL OR p.budget >= :budgetMin) AND " +
                     "(:budgetMax IS NULL OR p.budget <= :budgetMax)")
       Page<Projet> searchProjects(
                     @Param("nom") String nom,
                     @Param("statut") Projet.StatutProjet statut,
                     @Param("responsable") String responsable,
                     @Param("typeProjetId") Long typeProjetId,
                     @Param("dateDebutFrom") LocalDate dateDebutFrom,
                     @Param("dateDebutTo") LocalDate dateDebutTo,
                     @Param("budgetMin") BigDecimal budgetMin,
                     @Param("budgetMax") BigDecimal budgetMax,
                     Pageable pageable);

       // Recherche par tags
       @Query("SELECT p FROM Projet p WHERE LOWER(p.tags) LIKE LOWER(CONCAT('%', :tag, '%'))")
       Page<Projet> findByTagContaining(@Param("tag") String tag, Pageable pageable);

       // Statistiques
       @Query("SELECT COUNT(p) FROM Projet p WHERE p.statut = :statut")
       long countByStatut(@Param("statut") Projet.StatutProjet statut);

       @Query("SELECT p.statut, COUNT(p) FROM Projet p GROUP BY p.statut")
       List<Object[]> countProjectsByStatut();

       @Query("SELECT p.priorite, COUNT(p) FROM Projet p GROUP BY p.priorite")
       List<Object[]> countProjectsByPriorite();

       // Recherche par équipe
       @Query("SELECT p FROM Projet p WHERE :membre MEMBER OF p.equipe")
       Page<Projet> findByEquipeContaining(@Param("membre") String membre, Pageable pageable);

       // Projets en retard
       @Query("SELECT p FROM Projet p WHERE p.statut = 'EN_COURS' AND p.dateFinPrevue < CURRENT_DATE")
       List<Projet> findDelayedProjects();

       // Budget total par statut
       @Query("SELECT p.statut, SUM(p.budget) FROM Projet p GROUP BY p.statut")
       List<Object[]> getTotalBudgetByStatut();

       // Projets par mois
       @Query("SELECT FUNCTION('MONTH', p.dateDebut), FUNCTION('YEAR', p.dateDebut), COUNT(p) " +
                     "FROM Projet p GROUP BY FUNCTION('MONTH', p.dateDebut), FUNCTION('YEAR', p.dateDebut)")
       List<Object[]> countProjectsByMonth();

       Optional<Projet> findByNomIgnoreCase(String nom);

       boolean existsByNomIgnoreCaseAndSiteIdAndDateDebut(String nom, Long siteId, LocalDate dateDebut);
}