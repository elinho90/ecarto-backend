package com.gs2e.stage_eranove_academy.phase.service;

import com.gs2e.stage_eranove_academy.phase.dto.PhaseDto;
import com.gs2e.stage_eranove_academy.phase.model.Phase;
import com.gs2e.stage_eranove_academy.phase.model.StatutPhase;
import com.gs2e.stage_eranove_academy.phase.repository.PhaseRepository;
import com.gs2e.stage_eranove_academy.projet.model.Projet;
import com.gs2e.stage_eranove_academy.projet.repository.ProjetRepository;
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
public class PhaseService {

    private final PhaseRepository phaseRepository;
    private final ProjetRepository projetRepository;

    @Transactional(readOnly = true)
    public List<PhaseDto> getPhasesByProjet(Long projetId) {
        return phaseRepository.findByProjetIdOrderByOrdre(projetId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PhaseDto getPhaseById(Long id) {
        Phase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Phase non trouvée avec l'ID: " + id));
        return toDto(phase);
    }

    @Transactional
    public PhaseDto createPhase(PhaseDto dto) {
        Projet projet = projetRepository.findById(dto.getProjetId())
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + dto.getProjetId()));

        Phase phase = new Phase();
        phase.setNom(dto.getNom());
        phase.setDescription(dto.getDescription());
        
        long count = phaseRepository.countByProjetId(projet.getId());
        phase.setOrdre(dto.getOrdre() != null ? dto.getOrdre() : (int) count + 1);
        
        phase.setProjet(projet);
        phase.setDateDebutPrevue(dto.getDateDebutPrevue());
        phase.setDateFinPrevue(dto.getDateFinPrevue());
        phase.setStatut(StatutPhase.A_VENIR);
        phase.setProgression(0);
        phase.setVerrouillee(dto.getVerrouillee() != null ? dto.getVerrouillee() : false);

        Phase saved = phaseRepository.save(phase);
        return toDto(saved);
    }

    @Transactional
    public PhaseDto updatePhase(Long id, PhaseDto dto) {
        Phase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Phase non trouvée avec l'ID: " + id));

        phase.setNom(dto.getNom());
        phase.setDescription(dto.getDescription());
        
        if (dto.getOrdre() != null) phase.setOrdre(dto.getOrdre());
        if (dto.getDateDebutPrevue() != null) phase.setDateDebutPrevue(dto.getDateDebutPrevue());
        if (dto.getDateFinPrevue() != null) phase.setDateFinPrevue(dto.getDateFinPrevue());
        if (dto.getVerrouillee() != null) phase.setVerrouillee(dto.getVerrouillee());

        Phase saved = phaseRepository.save(phase);
        return toDto(saved);
    }

    @Transactional
    public void deletePhase(Long id) {
        if (!phaseRepository.existsById(id)) {
            throw new EntityNotFoundException("Phase non trouvée avec l'ID: " + id);
        }
        phaseRepository.deleteById(id);
    }

    public PhaseDto toDto(Phase phase) {
        PhaseDto dto = new PhaseDto();
        dto.setId(phase.getId());
        dto.setNom(phase.getNom());
        dto.setDescription(phase.getDescription());
        dto.setOrdre(phase.getOrdre());
        dto.setProjetId(phase.getProjet().getId());
        dto.setProjetNom(phase.getProjet().getNom());
        dto.setDateDebutPrevue(phase.getDateDebutPrevue());
        dto.setDateFinPrevue(phase.getDateFinPrevue());
        dto.setDateDebutReelle(phase.getDateDebutReelle());
        dto.setDateFinReelle(phase.getDateFinReelle());
        dto.setProgression(phase.getProgression());
        dto.setStatut(phase.getStatut());
        dto.setVerrouillee(phase.getVerrouillee());
        
        // Champs calculés
        if (phase.getEtapes() != null) {
            dto.setTotalEtapes(phase.getEtapes().size());
            long validees = phase.getEtapes().stream()
                .filter(e -> e.getStatut() == com.gs2e.stage_eranove_academy.etape.model.StatutEtape.VALIDEE)
                .count();
            dto.setEtapesValidees((int) validees);
            
            long retards = phase.getEtapes().stream()
                .filter(e -> e.isEnRetard())
                .count();
            dto.setEtapesEnRetard((int) retards);
        } else {
            dto.setTotalEtapes(0);
            dto.setEtapesValidees(0);
            dto.setEtapesEnRetard(0);
        }
        
        return dto;
    }
}
