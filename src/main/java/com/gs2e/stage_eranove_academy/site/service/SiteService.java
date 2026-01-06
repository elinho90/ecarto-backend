package com.gs2e.stage_eranove_academy.site.service;

import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.common.Exceptions.InvalidOperationException;
import com.gs2e.stage_eranove_academy.site.dto.SiteDto;
import com.gs2e.stage_eranove_academy.site.model.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface SiteService {

    Page<SiteDto> getAllSites(Pageable pageable);

    SiteDto getSiteById(Long id) throws EntityNotFoundException;

    SiteDto createSite(SiteDto siteDto) throws InvalidOperationException;

    SiteDto updateSite(Long id, SiteDto siteDto) throws EntityNotFoundException, InvalidOperationException;

    void deleteSite(Long id) throws EntityNotFoundException;

    Page<SiteDto> searchSites(String nom, String ville, String region, Site.TypeSite type, Site.StatutSite statut, Pageable pageable);

    Page<SiteDto> getSitesByType(Site.TypeSite type, Pageable pageable);

    Page<SiteDto> getSitesByStatut(Site.StatutSite statut, Pageable pageable);

    List<SiteDto> getSitesInBounds(Double latMin, Double latMax, Double lngMin, Double lngMax);

    List<String> getAllRegions();

    List<String> getVillesByRegion(String region);

    Map<String, Object> getSiteStatistics();

    Map<String, Long> getSitesCountByType();

    Map<String, Long> getSitesCountByStatut();
}