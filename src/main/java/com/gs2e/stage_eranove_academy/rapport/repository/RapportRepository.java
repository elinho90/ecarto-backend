package com.gs2e.stage_eranove_academy.rapport.repository;

import com.gs2e.stage_eranove_academy.rapport.model.Rapport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface RapportRepository extends JpaRepository<Rapport, Long> {

    Page<Rapport> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    Page<Rapport> findByFichierType(String fichierType, Pageable pageable);

    Page<Rapport> findByProjetId(Long projetId, Pageable pageable);

    Page<Rapport> findByUploadeParContainingIgnoreCase(String uploadePar, Pageable pageable);

    Page<Rapport> findByRisque(Rapport.NiveauRisque risque, Pageable pageable);

    @Query("SELECT r FROM Rapport r WHERE r.faisabilite >= :minFaisabilite AND r.faisabilite <= :maxFaisabilite")
    Page<Rapport> findByFaisabiliteBetween(
            @Param("minFaisabilite") Integer minFaisabilite,
            @Param("maxFaisabilite") Integer maxFaisabilite,
            Pageable pageable
    );

    @Query("SELECT r FROM Rapport r WHERE " +
            "(:nom IS NULL OR LOWER(r.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
            "(:projetId IS NULL OR r.projet.id = :projetId) AND " +
            "(:fichierType IS NULL OR r.fichierType = :fichierType) AND " +
            "(:uploadePar IS NULL OR LOWER(r.uploadePar) LIKE LOWER(CONCAT('%', :uploadePar, '%'))) AND " +
            "(:risque IS NULL OR r.risque = :risque) AND " +
            "(:minFaisabilite IS NULL OR r.faisabilite >= :minFaisabilite) AND " +
            "(:maxFaisabilite IS NULL OR r.faisabilite <= :maxFaisabilite) AND " +
            "(:uploadDateFrom IS NULL OR r.createdAt >= :uploadDateFrom) AND " +
            "(:uploadDateTo IS NULL OR r.createdAt <= :uploadDateTo)")
    Page<Rapport> searchRapports(
            @Param("nom") String nom,
            @Param("projetId") Long projetId,
            @Param("fichierType") String fichierType,
            @Param("uploadePar") String uploadePar,
            @Param("risque") Rapport.NiveauRisque risque,
            @Param("minFaisabilite") Integer minFaisabilite,
            @Param("maxFaisabilite") Integer maxFaisabilite,
            @Param("uploadDateFrom") LocalDateTime uploadDateFrom,
            @Param("uploadDateTo") LocalDateTime uploadDateTo,
            Pageable pageable
    );

    @Query("SELECT r.fichierType, COUNT(r) FROM Rapport r GROUP BY r.fichierType")
    Page<Object[]> countByFileType(Pageable pageable);

    @Query("SELECT r.risque, COUNT(r) FROM Rapport r GROUP BY r.risque")
    Page<Object[]> countByRiskLevel(Pageable pageable);

    @Query("SELECT AVG(r.faisabilite) FROM Rapport r")
    Double getAverageFaisabilite();

    @Query("SELECT SUM(r.budgetEstime) FROM Rapport r WHERE r.budgetEstime IS NOT NULL")
    BigDecimal getTotalBudgetEstime();

    @Query("SELECT r FROM Rapport r WHERE r.createdAt >= :dateDebut AND r.createdAt <= :dateFin")
    Page<Rapport> findByUploadDateBetween(
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            Pageable pageable
    );

    @Query("SELECT r FROM Rapport r ORDER BY r.createdAt DESC")
    Page<Rapport> findRecentRapports(Pageable pageable);

    @Query("SELECT r FROM Rapport r WHERE r.projet IS NULL")
    Page<Rapport> findRapportsWithoutProject(Pageable pageable);

    boolean existsByFichierNom(String fichierNom);

    @Query("SELECT SUM(r.budgetEstime) FROM Rapport r WHERE r.risque = :risque AND r.budgetEstime IS NOT NULL")
    BigDecimal sumBudgetByRiskLevel(@Param("risque") Rapport.NiveauRisque risque);

    @Query("SELECT r FROM Rapport r WHERE r.fichierTaille >= :minSize AND r.fichierTaille <= :maxSize")
    Page<Rapport> findByFileSizeBetween(
            @Param("minSize") Long minSize,
            @Param("maxSize") Long maxSize,
            Pageable pageable
    );
}