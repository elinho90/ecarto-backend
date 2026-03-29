package com.gs2e.stage_eranove_academy.projet.mapper;

import com.gs2e.stage_eranove_academy.projet.dto.ProjetDto;
import com.gs2e.stage_eranove_academy.projet.model.Projet;
import com.gs2e.stage_eranove_academy.typeprojet.model.TypeProjet;
import org.hibernate.Hibernate;
import org.mapstruct.*;

import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjetMapper {

    @Mapping(target = "typeProjetId", expression = "java(getTypeProjetIdSafe(projet))")
    @Mapping(target = "typeProjetNom", expression = "java(getTypeProjetLibelleSafe(projet))")
    @Mapping(target = "siteId", expression = "java(getSiteIdSafe(projet))")
    @Mapping(target = "siteNom", expression = "java(getSiteLibelleSafe(projet))")
    @Mapping(target = "comiteId", expression = "java(getComiteIdSafe(projet))")
    @Mapping(target = "comiteNom", expression = "java(getComiteLibelleSafe(projet))")
    @Mapping(target = "entiteId", expression = "java(getEntiteIdSafe(projet))")
    @Mapping(target = "entiteNom", expression = "java(getEntiteLibelleSafe(projet))")
    @Mapping(target = "dureeJours", expression = "java(calculateDuration(projet))")
    @Mapping(target = "coutParJour", expression = "java(calculateCostPerDay(projet))")
    ProjetDto toDto(Projet projet);

    @Mapping(target = "typeProjet", expression = "java(mapTypeProjet(projetDto.getTypeProjetId()))")
    @Mapping(target = "site", expression = "java(mapSite(projetDto.getSiteId()))")
    @Mapping(target = "comite", expression = "java(mapComite(projetDto.getComiteId()))")
    @Mapping(target = "entite", expression = "java(mapEntite(projetDto.getEntiteId()))")
    Projet toEntity(ProjetDto projetDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProjetDto dto, @MappingTarget Projet entity);

    default TypeProjet mapTypeProjet(Long typeProjetId) {
        if (typeProjetId == null) {
            return null;
        }
        TypeProjet typeProjet = new TypeProjet();
        typeProjet.setId(typeProjetId);
        return typeProjet;
    }

    default com.gs2e.stage_eranove_academy.site.model.Site mapSite(Long siteId) {
        if (siteId == null) {
            return null;
        }
        com.gs2e.stage_eranove_academy.site.model.Site site = new com.gs2e.stage_eranove_academy.site.model.Site();
        site.setId(siteId);
        return site;
    }

    default com.gs2e.stage_eranove_academy.comite.model.Comite mapComite(Long comiteId) {
        if (comiteId == null) {
            return null;
        }
        com.gs2e.stage_eranove_academy.comite.model.Comite comite = new com.gs2e.stage_eranove_academy.comite.model.Comite();
        comite.setId(comiteId);
        return comite;
    }

    default com.gs2e.stage_eranove_academy.entite.model.Entite mapEntite(Long entiteId) {
        if (entiteId == null) {
            return null;
        }
        com.gs2e.stage_eranove_academy.entite.model.Entite entite = new com.gs2e.stage_eranove_academy.entite.model.Entite();
        entite.setId(entiteId);
        return entite;
    }

    default Long calculateDuration(Projet projet) {
        if (projet.getDateDebut() == null || projet.getDateFinPrevue() == null) {
            return null;
        }
        return ChronoUnit.DAYS.between(projet.getDateDebut(), projet.getDateFinPrevue());
    }

    default java.math.BigDecimal calculateCostPerDay(Projet projet) {
        if (projet.getBudget() == null || projet.getDateDebut() == null || projet.getDateFinPrevue() == null) {
            return null;
        }
        long days = ChronoUnit.DAYS.between(projet.getDateDebut(), projet.getDateFinPrevue());
        if (days == 0) {
            return null;
        }
        return projet.getBudget().divide(java.math.BigDecimal.valueOf(days), 2, java.math.RoundingMode.HALF_UP);
    }

    default String getTypeProjetLibelleSafe(Projet projet) {
        try {
            TypeProjet typeProjet = projet.getTypeProjet();
            if (typeProjet != null) {
                if (!Hibernate.isInitialized(typeProjet)) {
                    Hibernate.initialize(typeProjet);
                }
                return typeProjet.getNom();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    default Long getTypeProjetIdSafe(Projet projet) {
        try {
            TypeProjet typeProjet = projet.getTypeProjet();
            if (typeProjet != null) {
                if (!Hibernate.isInitialized(typeProjet)) {
                    Hibernate.initialize(typeProjet);
                }
                return typeProjet.getId();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    default String getSiteLibelleSafe(Projet projet) {
        try {
            com.gs2e.stage_eranove_academy.site.model.Site site = projet.getSite();
            if (site != null) {
                if (!Hibernate.isInitialized(site)) {
                    Hibernate.initialize(site);
                }
                return site.getNom();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    default Long getSiteIdSafe(Projet projet) {
        try {
            com.gs2e.stage_eranove_academy.site.model.Site site = projet.getSite();
            if (site != null) {
                if (!Hibernate.isInitialized(site)) {
                    Hibernate.initialize(site);
                }
                return site.getId();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    default String getComiteLibelleSafe(Projet projet) {
        try {
            com.gs2e.stage_eranove_academy.comite.model.Comite comite = projet.getComite();
            if (comite != null) {
                if (!Hibernate.isInitialized(comite)) {
                    Hibernate.initialize(comite);
                }
                return comite.getNom();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    default Long getComiteIdSafe(Projet projet) {
        try {
            com.gs2e.stage_eranove_academy.comite.model.Comite comite = projet.getComite();
            if (comite != null) {
                if (!Hibernate.isInitialized(comite)) {
                    Hibernate.initialize(comite);
                }
                return comite.getId();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    default String getEntiteLibelleSafe(Projet projet) {
        try {
            com.gs2e.stage_eranove_academy.entite.model.Entite entite = projet.getEntite();
            if (entite != null) {
                if (!Hibernate.isInitialized(entite)) {
                    Hibernate.initialize(entite);
                }
                return entite.getNom();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    default Long getEntiteIdSafe(Projet projet) {
        try {
            com.gs2e.stage_eranove_academy.entite.model.Entite entite = projet.getEntite();
            if (entite != null) {
                if (!Hibernate.isInitialized(entite)) {
                    Hibernate.initialize(entite);
                }
                return entite.getId();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}