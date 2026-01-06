package com.gs2e.stage_eranove_academy.rapport.mapper;

import com.gs2e.stage_eranove_academy.rapport.dto.RapportDto;
import com.gs2e.stage_eranove_academy.rapport.model.Rapport;
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

        // Mapping du projet
        if (rapport.getProjet() != null) {
            dto.setProjetId(rapport.getProjet().getId());
            dto.setProjetNom(rapport.getProjet().getNom());
        }

        // Champs calculés
        dto.setFichierTailleFormatee(formatFileSize(rapport.getFichierTaille()));
        dto.setDureeEstimeeFormatee(formatDuration(rapport.getDureeEstimeeMois()));

        return dto;
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