package com.gs2e.stage_eranove_academy.rapport.mapper;

import com.gs2e.stage_eranove_academy.projet.model.Projet;
import com.gs2e.stage_eranove_academy.rapport.dto.RapportDto;
import com.gs2e.stage_eranove_academy.rapport.model.Rapport;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

@Component
public class RapportMapper {

    public RapportDto toDto(Rapport rapport) {
        if (rapport == null) {
            return null;
        }

        RapportDto dto = new RapportDto();
        dto.setId(rapport.getId());
        dto.setNom(rapport.getNom());
        dto.setDescription(rapport.getDescription());
        dto.setFichierNom(rapport.getFichierNom());
        dto.setFichierType(rapport.getFichierType());
        dto.setFichierTaille(rapport.getFichierTaille());
        dto.setUploadePar(rapport.getUploadePar());
        dto.setFaisabilite(rapport.getFaisabilite());
        dto.setRisque(rapport.getRisque());
        dto.setBudgetEstime(rapport.getBudgetEstime());
        dto.setDureeEstimeeMois(rapport.getDureeEstimeeMois());
        dto.setRecommandations(rapport.getRecommandations());
        dto.setAnalyseAutomatique(rapport.getAnalyseAutomatique());

        // Mapping du projet avec vérification Hibernate pour éviter
        // LazyInitializationException
        dto.setProjetId(getProjetIdSafe(rapport));
        dto.setProjetNom(getProjetNomSafe(rapport));

        // Champs calculés
        dto.setFichierTailleFormatee(formatFileSize(rapport.getFichierTaille()));
        dto.setDureeEstimeeFormatee(formatDuration(rapport.getDureeEstimeeMois()));

        return dto;
    }

    /**
     * Récupère l'ID du projet de manière sécurisée pour éviter
     * LazyInitializationException
     */
    private Long getProjetIdSafe(Rapport rapport) {
        try {
            Projet projet = rapport.getProjet();
            if (projet != null) {
                if (!Hibernate.isInitialized(projet)) {
                    // Si le projet n'est pas initialisé, on ne peut pas accéder à ses propriétés
                    // sauf l'ID qui est dans la clé étrangère
                    return projet.getId();
                }
                return projet.getId();
            }
        } catch (Exception e) {
            // En cas d'erreur, retourner null
            return null;
        }
        return null;
    }

    /**
     * Récupère le nom du projet de manière sécurisée pour éviter
     * LazyInitializationException
     */
    private String getProjetNomSafe(Rapport rapport) {
        try {
            Projet projet = rapport.getProjet();
            if (projet != null) {
                if (!Hibernate.isInitialized(projet)) {
                    // Si le projet n'est pas initialisé, on ne peut pas accéder au nom
                    return null;
                }
                return projet.getNom();
            }
        } catch (Exception e) {
            // En cas d'erreur, retourner null
            return null;
        }
        return null;
    }

    public Rapport toEntity(RapportDto dto) {
        if (dto == null) {
            return null;
        }

        Rapport rapport = new Rapport();
        rapport.setId(dto.getId());
        rapport.setNom(dto.getNom());
        rapport.setDescription(dto.getDescription());
        rapport.setFichierNom(dto.getFichierNom());
        rapport.setFichierType(dto.getFichierType());
        rapport.setFichierTaille(dto.getFichierTaille());
        rapport.setUploadePar(dto.getUploadePar());
        rapport.setFaisabilite(dto.getFaisabilite());
        rapport.setRisque(dto.getRisque());
        rapport.setBudgetEstime(dto.getBudgetEstime());
        rapport.setDureeEstimeeMois(dto.getDureeEstimeeMois());
        rapport.setRecommandations(dto.getRecommandations());
        rapport.setAnalyseAutomatique(dto.getAnalyseAutomatique() != null ? dto.getAnalyseAutomatique() : false);

        return rapport;
    }

    public void updateEntityFromDto(RapportDto dto, Rapport rapport) {
        if (dto == null || rapport == null) {
            return;
        }

        rapport.setNom(dto.getNom());
        rapport.setDescription(dto.getDescription());
        rapport.setFaisabilite(dto.getFaisabilite());
        rapport.setRisque(dto.getRisque());
        rapport.setBudgetEstime(dto.getBudgetEstime());
        rapport.setDureeEstimeeMois(dto.getDureeEstimeeMois());
        rapport.setRecommandations(dto.getRecommandations());
    }

    private String formatFileSize(Long sizeInBytes) {
        if (sizeInBytes == null || sizeInBytes <= 0)
            return "0 B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(sizeInBytes) / Math.log10(1024));
        return String.format("%.1f %s", sizeInBytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    private String formatDuration(Integer months) {
        if (months == null || months <= 0)
            return "0 mois";
        if (months < 12)
            return months + " mois";
        int years = months / 12;
        int remainingMonths = months % 12;
        if (remainingMonths == 0)
            return years + " an" + (years > 1 ? "s" : "");
        return years + " an" + (years > 1 ? "s" : "") + " " + remainingMonths + " mois";
    }
}