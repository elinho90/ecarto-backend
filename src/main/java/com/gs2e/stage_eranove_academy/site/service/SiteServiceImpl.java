package com.gs2e.stage_eranove_academy.site.service;

import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.common.Exceptions.InvalidOperationException;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import com.gs2e.stage_eranove_academy.security.service.AuthService;
import com.gs2e.stage_eranove_academy.security.service.NotificationService;
import com.gs2e.stage_eranove_academy.site.dto.SiteDto;
import com.gs2e.stage_eranove_academy.site.mapper.SiteMapper;
import com.gs2e.stage_eranove_academy.site.model.Site;
import com.gs2e.stage_eranove_academy.site.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;
    private final SiteMapper siteMapper;
    private final NotificationService notificationService;
    private final AuthService authService;

    @Override
    @Transactional(readOnly = true)
    public Page<SiteDto> getAllSites(Pageable pageable) {
        log.info("Récupération de tous les sites - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Site> sites = siteRepository.findAll(pageable);
        return sites.map(siteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public SiteDto getSiteById(Long id) throws EntityNotFoundException {
        log.info("Récupération du site avec l'ID: {}", id);
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Site non trouvé avec l'ID: " + id));
        return siteMapper.toDto(site);
    }

    @Override
    public SiteDto createSite(SiteDto siteDto) throws InvalidOperationException {
        log.info("Création d'un nouveau site: {}", siteDto.getNom());

        if (siteDto.getNom() != null && siteDto.getVille() != null) {
            List<Site> existingSites = siteRepository.findByNomContainingIgnoreCase(
                    siteDto.getNom(), Pageable.unpaged()).getContent();

            boolean duplicateExists = existingSites.stream()
                    .anyMatch(s -> s.getVille().equalsIgnoreCase(siteDto.getVille()));

            if (duplicateExists) {
                throw new InvalidOperationException(
                        "Un site avec le nom '" + siteDto.getNom() +
                                "' existe déjà dans la ville de " + siteDto.getVille());
            }
        }

        Site site = siteMapper.toEntity(siteDto);
        Site savedSite = siteRepository.save(site);
        log.info("Site créé avec succès - ID: {}", savedSite.getId());

        // Notification de création
        try {
            Utilisateur currentUser = authService.getCurrentUser();
            notificationService.createNotification(
                    currentUser,
                    "SITE_CREATION",
                    "Nouveau site créé",
                    "Le site '" + savedSite.getNom() + "' à " + savedSite.getVille() + " a été créé.");
        } catch (Exception e) {
            log.warn("Impossible de créer la notification de création de site: {}", e.getMessage());
        }

        return siteMapper.toDto(savedSite);
    }

    @Override
    public SiteDto updateSite(Long id, SiteDto siteDto)
            throws EntityNotFoundException, InvalidOperationException {
        log.info("Mise à jour du site avec l'ID: {}", id);

        Site existingSite = siteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Site non trouvé avec l'ID: " + id));

        if (siteDto.getNom() != null && siteDto.getVille() != null) {
            List<Site> sitesWithSameName = siteRepository.findByNomContainingIgnoreCase(
                    siteDto.getNom(), Pageable.unpaged()).getContent();

            boolean duplicateExists = sitesWithSameName.stream()
                    .anyMatch(s -> !s.getId().equals(id) &&
                            s.getVille().equalsIgnoreCase(siteDto.getVille()));

            if (duplicateExists) {
                throw new InvalidOperationException(
                        "Un autre site avec le nom '" + siteDto.getNom() +
                                "' existe déjà dans la ville de " + siteDto.getVille());
            }
        }

        siteMapper.updateEntityFromDto(siteDto, existingSite);
        Site updatedSite = siteRepository.save(existingSite);
        log.info("Site mis à jour avec succès - ID: {}", updatedSite.getId());

        // Notification de mise à jour
        try {
            Utilisateur currentUser = authService.getCurrentUser();
            notificationService.createNotification(
                    currentUser,
                    "SITE_UPDATE",
                    "Site mis à jour",
                    "Le site '" + updatedSite.getNom() + "' a été mis à jour.");
        } catch (Exception e) {
            log.warn("Impossible de créer la notification de mise à jour de site: {}", e.getMessage());
        }

        return siteMapper.toDto(updatedSite);
    }

    @Override
    public void deleteSite(Long id) throws EntityNotFoundException {
        log.info("Suppression du site avec l'ID: {}", id);

        if (!siteRepository.existsById(id)) {
            throw new EntityNotFoundException("Site non trouvé avec l'ID: " + id);
        }

        Site site = siteRepository.findById(id).orElse(null);
        String siteNom = (site != null) ? site.getNom() : String.valueOf(id);

        siteRepository.deleteById(id);
        log.info("Site supprimé avec succès - ID: {}", id);

        // Notification de suppression
        try {
            Utilisateur currentUser = authService.getCurrentUser();
            notificationService.createNotification(
                    currentUser,
                    "SITE_DELETION",
                    "Site supprimé",
                    "Le site '" + siteNom + "' a été supprimé.");
        } catch (Exception e) {
            log.warn("Impossible de créer la notification de suppression de site: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SiteDto> searchSites(String nom, String ville, String region,
            Site.TypeSite type, Site.StatutSite statut,
            Pageable pageable) {
        log.info("Recherche de sites avec critères - nom: {}, ville: {}, région: {}, type: {}, statut: {}",
                nom, ville, region, type, statut);

        Page<Site> sites = siteRepository.searchSites(nom, ville, region, type, statut, pageable);
        return sites.map(siteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SiteDto> getSitesByType(Site.TypeSite type, Pageable pageable) {
        log.info("Récupération des sites par type: {}", type);
        Page<Site> sites = siteRepository.findByType(type, pageable);
        return sites.map(siteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SiteDto> getSitesByStatut(Site.StatutSite statut, Pageable pageable) {
        log.info("Récupération des sites par statut: {}", statut);
        Page<Site> sites = siteRepository.findByStatut(statut, pageable);
        return sites.map(siteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteDto> getSitesInBounds(Double latMin, Double latMax,
            Double lngMin, Double lngMax) {
        log.info("Récupération des sites dans les limites - lat: [{}, {}], lng: [{}, {}]",
                latMin, latMax, lngMin, lngMax);

        List<Site> sites = siteRepository.findSitesInBounds(latMin, latMax, lngMin, lngMax);
        return sites.stream()
                .map(siteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllRegions() {
        log.info("Récupération de toutes les régions");
        return siteRepository.findAllRegions();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getVillesByRegion(String region) {
        log.info("Récupération des villes pour la région: {}", region);
        return siteRepository.findVillesByRegion(region);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSiteStatistics() {
        log.info("Récupération des statistiques des sites");

        Map<String, Object> statistics = new HashMap<>();

        long totalSites = siteRepository.count();
        statistics.put("totalSites", totalSites);

        Map<String, Long> sitesByType = getSitesCountByType();
        statistics.put("sitesByType", sitesByType);

        Map<String, Long> sitesByStatut = getSitesCountByStatut();
        statistics.put("sitesByStatut", sitesByStatut);

        long totalRegions = siteRepository.findAllRegions().size();
        statistics.put("totalRegions", totalRegions);

        long sitesActifs = siteRepository.countByStatut(Site.StatutSite.ACTIF);
        statistics.put("sitesActifs", sitesActifs);

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getSitesCountByType() {
        log.info("Récupération du nombre de sites par type");

        List<Object[]> results = siteRepository.countSitesByType();
        Map<String, Long> countByType = new HashMap<>();

        for (Object[] result : results) {
            Site.TypeSite type = (Site.TypeSite) result[0];
            Long count = (Long) result[1];
            countByType.put(type.name(), count);
        }

        return countByType;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getSitesCountByStatut() {
        log.info("Récupération du nombre de sites par statut");

        List<Object[]> results = siteRepository.countSitesByStatut();
        Map<String, Long> countByStatut = new HashMap<>();

        for (Object[] result : results) {
            Site.StatutSite statut = (Site.StatutSite) result[0];
            Long count = (Long) result[1];
            countByStatut.put(statut.name(), count);
        }

        return countByStatut;
    }
}