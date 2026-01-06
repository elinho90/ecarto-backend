package com.gs2e.stage_eranove_academy.site.repository;

import com.gs2e.stage_eranove_academy.site.model.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

    Page<Site> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    Page<Site> findByVilleContainingIgnoreCase(String ville, Pageable pageable);

    Page<Site> findByRegionContainingIgnoreCase(String region, Pageable pageable);

    Page<Site> findByType(Site.TypeSite type, Pageable pageable);

    Page<Site> findByStatut(Site.StatutSite statut, Pageable pageable);

    @Query("SELECT s FROM Site s WHERE " +
           "(:nom IS NULL OR LOWER(s.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:ville IS NULL OR LOWER(s.ville) LIKE LOWER(CONCAT('%', :ville, '%'))) AND " +
           "(:region IS NULL OR LOWER(s.region) LIKE LOWER(CONCAT('%', :region, '%'))) AND " +
           "(:type IS NULL OR s.type = :type) AND " +
           "(:statut IS NULL OR s.statut = :statut)")
    Page<Site> searchSites(
            @Param("nom") String nom,
            @Param("ville") String ville,
            @Param("region") String region,
            @Param("type") Site.TypeSite type,
            @Param("statut") Site.StatutSite statut,
            Pageable pageable
    );

    @Query("SELECT s FROM Site s WHERE s.latitude BETWEEN :latMin AND :latMax AND s.longitude BETWEEN :lngMin AND :lngMax")
    List<Site> findSitesInBounds(
            @Param("latMin") Double latMin,
            @Param("latMax") Double latMax,
            @Param("lngMin") Double lngMin,
            @Param("lngMax") Double lngMax
    );

    @Query("SELECT DISTINCT s.region FROM Site s ORDER BY s.region")
    List<String> findAllRegions();

    @Query("SELECT DISTINCT s.ville FROM Site s WHERE s.region = :region ORDER BY s.ville")
    List<String> findVillesByRegion(@Param("region") String region);

    long countByType(Site.TypeSite type);

    long countByStatut(Site.StatutSite statut);

    @Query("SELECT s.type, COUNT(s) FROM Site s GROUP BY s.type")
    List<Object[]> countSitesByType();

    @Query("SELECT s.statut, COUNT(s) FROM Site s GROUP BY s.statut")
    List<Object[]> countSitesByStatut();
}