package com.gs2e.stage_eranove_academy.site.mapper;

import com.gs2e.stage_eranove_academy.site.dto.SiteDto;
import com.gs2e.stage_eranove_academy.site.model.Site;
import org.springframework.stereotype.Component;

@Component
public class SiteMapper {

    /**
     * Convertit une entité Site en DTO
     */
    public SiteDto toDto(Site site) {
        if (site == null) {
            return null;
        }

        SiteDto dto = new SiteDto();
        dto.setId(site.getId());
        dto.setNom(site.getNom());
        dto.setDescription(site.getDescription());
        dto.setAdresse(site.getAdresse());
        dto.setVille(site.getVille());
        dto.setRegion(site.getRegion());
        dto.setPays(site.getPays());
        dto.setLatitude(site.getLatitude());
        dto.setLongitude(site.getLongitude());
        dto.setType(site.getType());
        dto.setStatut(site.getStatut());
        dto.setContactPersonne(site.getContactPersonne());
        dto.setContactTelephone(site.getContactTelephone());
        dto.setContactEmail(site.getContactEmail());
        dto.setNombreEmployes(site.getNombreEmployes());
        dto.setHorairesOuverture(site.getHorairesOuverture());
        dto.setEquipements(site.getEquipements());

        return dto;
    }

    /**
     * Convertit un DTO en entité Site
     */
    public Site toEntity(SiteDto dto) {
        if (dto == null) {
            return null;
        }

        Site site = new Site();
        site.setId(dto.getId());
        site.setNom(dto.getNom());
        site.setDescription(dto.getDescription());
        site.setAdresse(dto.getAdresse());
        site.setVille(dto.getVille());
        site.setRegion(dto.getRegion());
        site.setPays(dto.getPays() != null ? dto.getPays() : "Côte d'Ivoire");
        site.setLatitude(dto.getLatitude());
        site.setLongitude(dto.getLongitude());
        site.setType(dto.getType());
        site.setStatut(dto.getStatut() != null ? dto.getStatut() : Site.StatutSite.ACTIF);
        site.setContactPersonne(dto.getContactPersonne());
        site.setContactTelephone(dto.getContactTelephone());
        site.setContactEmail(dto.getContactEmail());
        site.setNombreEmployes(dto.getNombreEmployes());
        site.setHorairesOuverture(dto.getHorairesOuverture());
        site.setEquipements(dto.getEquipements());

        return site;
    }

    /**
     * Met à jour une entité Site existante avec les données d'un DTO
     */
    public void updateEntityFromDto(SiteDto dto, Site site) {
        if (dto == null || site == null) {
            return;
        }

        site.setNom(dto.getNom());
        site.setDescription(dto.getDescription());
        site.setAdresse(dto.getAdresse());
        site.setVille(dto.getVille());
        site.setRegion(dto.getRegion());
        site.setPays(dto.getPays());
        site.setLatitude(dto.getLatitude());
        site.setLongitude(dto.getLongitude());
        site.setType(dto.getType());
        site.setStatut(dto.getStatut());
        site.setContactPersonne(dto.getContactPersonne());
        site.setContactTelephone(dto.getContactTelephone());
        site.setContactEmail(dto.getContactEmail());
        site.setNombreEmployes(dto.getNombreEmployes());
        site.setHorairesOuverture(dto.getHorairesOuverture());
        site.setEquipements(dto.getEquipements());
    }
}