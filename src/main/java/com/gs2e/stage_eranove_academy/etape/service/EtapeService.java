package com.gs2e.stage_eranove_academy.etape.service;

import com.gs2e.stage_eranove_academy.etape.dto.EtapeDto;
import com.gs2e.stage_eranove_academy.etape.model.Etape;
import com.gs2e.stage_eranove_academy.etape.model.StatutEtape;
import com.gs2e.stage_eranove_academy.etape.repository.EtapeRepository;
import com.gs2e.stage_eranove_academy.phase.model.Phase;
import com.gs2e.stage_eranove_academy.phase.repository.PhaseRepository;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import com.gs2e.stage_eranove_academy.security.repository.UtilisateurRepository;
import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EtapeService {

    private final EtapeRepository etapeRepository;
    private final PhaseRepository phaseRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Transactional(readOnly = true)
    public List<EtapeDto> getEtapesByPhase(Long phaseId) {
        return etapeRepository.findByPhaseIdOrderByOrdre(phaseId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EtapeDto getEtapeById(Long id) {
        Etape etape = etapeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Étape non trouvée avec l'ID: " + id));
        return toDto(etape);
    }

    @Transactional(readOnly = true)
    public List<EtapeDto> getEtapesByResponsable(Long responsableId) {
        return etapeRepository.findByResponsableId(responsableId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EtapeDto createEtape(EtapeDto dto) {
        Phase phase = phaseRepository.findById(dto.getPhaseId())
                .orElseThrow(() -> new EntityNotFoundException("Phase non trouvée avec l'ID: " + dto.getPhaseId()));

        Etape etape = new Etape();
        etape.setNom(dto.getNom());
        etape.setDescription(dto.getDescription());
        
        if (dto.getOrdre() != null) {
            etape.setOrdre(dto.getOrdre());
        } else {
            int currentCount = etapeRepository.findByPhaseIdOrderByOrdre(phase.getId()).size();
            etape.setOrdre(currentCount + 1);
        }
        
        etape.setPhase(phase);
        
        if (dto.getResponsableId() != null) {
            Utilisateur responsable = utilisateurRepository.findById(dto.getResponsableId())
                    .orElseThrow(() -> new EntityNotFoundException("Utilsateur non trouvé (responsable): " + dto.getResponsableId()));
            etape.setResponsable(responsable);
        }
        
        etape.setDateEcheance(dto.getDateEcheance());
        etape.setDureeEstimeeJours(dto.getDureeEstimeeJours() != null ? dto.getDureeEstimeeJours() : 0);
        etape.setStatut(StatutEtape.A_FAIRE);
        etape.setValidationRequise(dto.getValidationRequise() != null ? dto.getValidationRequise() : true);
        etape.setBloquante(dto.getBloquante() != null ? dto.getBloquante() : false);
        
        if (dto.getTypeLivrable() != null) etape.setTypeLivrable(dto.getTypeLivrable());

        Etape saved = etapeRepository.save(etape);
        phase.getEtapes().add(saved);
        phase.recalculerProgression();
        phaseRepository.save(phase);

        return toDto(saved);
    }

    @Transactional
    public EtapeDto updateEtape(Long id, EtapeDto dto) {
        Etape etape = etapeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Étape non trouvée avec l'ID: " + id));

        etape.setNom(dto.getNom());
        etape.setDescription(dto.getDescription());
        if (dto.getOrdre() != null) etape.setOrdre(dto.getOrdre());
        if (dto.getDateEcheance() != null) etape.setDateEcheance(dto.getDateEcheance());
        if (dto.getDureeEstimeeJours() != null) etape.setDureeEstimeeJours(dto.getDureeEstimeeJours());
        if (dto.getValidationRequise() != null) etape.setValidationRequise(dto.getValidationRequise());
        if (dto.getBloquante() != null) etape.setBloquante(dto.getBloquante());
        if (dto.getTypeLivrable() != null) etape.setTypeLivrable(dto.getTypeLivrable());
        
        if (dto.getResponsableId() != null && (etape.getResponsable() == null || !etape.getResponsable().getId().equals(dto.getResponsableId()))) {
            Utilisateur responsable = utilisateurRepository.findById(dto.getResponsableId())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + dto.getResponsableId()));
            etape.setResponsable(responsable);
        }

        Etape saved = etapeRepository.save(etape);
        return toDto(saved);
    }

    @Transactional
    public void deleteEtape(Long id) {
        Etape etape = etapeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Étape non trouvée avec l'ID: " + id));
        Phase phase = etape.getPhase();
        etapeRepository.delete(etape);
        
        phase.getEtapes().remove(etape);
        phase.recalculerProgression();
        phaseRepository.save(phase);
    }

    public EtapeDto toDto(Etape etape) {
        EtapeDto dto = new EtapeDto();
        dto.setId(etape.getId());
        dto.setNom(etape.getNom());
        dto.setDescription(etape.getDescription());
        dto.setOrdre(etape.getOrdre());
        
        if (etape.getPhase() != null) {
            dto.setPhaseId(etape.getPhase().getId());
            dto.setPhaseNom(etape.getPhase().getNom());
        }
        
        if (etape.getResponsable() != null) {
            dto.setResponsableId(etape.getResponsable().getId());
            dto.setResponsableNom(etape.getResponsable().getNom() + " " + etape.getResponsable().getPrenom());
        }
        
        dto.setDateEcheance(etape.getDateEcheance());
        dto.setDateRealisation(etape.getDateRealisation());
        dto.setDureeEstimeeJours(etape.getDureeEstimeeJours());
        dto.setDureeReelleJours(etape.getDureeReelleJours());
        dto.setStatut(etape.getStatut());
        dto.setValidationRequise(etape.getValidationRequise());
        dto.setBloquante(etape.getBloquante());
        dto.setTypeLivrable(etape.getTypeLivrable());
        dto.setUrlLivrable(etape.getUrlLivrable());
        
        // Champs calculés
        dto.setEnRetard(etape.isEnRetard());
        dto.setJoursRetard(etape.getJoursRetard());
        dto.setJoursRestants(etape.getJoursRestants());
        
        return dto;
    }
}
