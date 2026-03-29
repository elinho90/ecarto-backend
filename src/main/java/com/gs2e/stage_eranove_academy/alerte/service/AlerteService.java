package com.gs2e.stage_eranove_academy.alerte.service;

import com.gs2e.stage_eranove_academy.alerte.dto.AlerteDto;
import com.gs2e.stage_eranove_academy.alerte.model.Alerte;
import com.gs2e.stage_eranove_academy.alerte.repository.AlerteRepository;
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
public class AlerteService {

    private final AlerteRepository alerteRepository;

    @Transactional(readOnly = true)
    public List<AlerteDto> getAlertesByProjet(Long projetId) {
        return alerteRepository.findByProjetIdOrderByCreatedAtDesc(projetId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlerteDto> getUnreadAlertesByDestinataire(Long destinataireId) {
        return alerteRepository.findByDestinataire_IdAndLueFalseOrderByCreatedAtDesc(destinataireId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlerteDto> getUnresolvedAlertesByProjet(Long projetId) {
        return alerteRepository.findByProjetIdAndResolueFalseOrderByCreatedAtDesc(projetId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countUnreadAlertesByDestinataire(Long destinataireId) {
        return alerteRepository.countByDestinataire_IdAndLueFalse(destinataireId);
    }

    @Transactional
    public AlerteDto markAsRead(Long id) {
        Alerte alerte = alerteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alerte non trouvée avec l'ID: " + id));
        alerte.setLue(true);
        return toDto(alerteRepository.save(alerte));
    }

    @Transactional
    public AlerteDto markAsResolved(Long id) {
        Alerte alerte = alerteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alerte non trouvée avec l'ID: " + id));
        alerte.setResolue(true);
        return toDto(alerteRepository.save(alerte));
    }

    @Transactional
    public void deleteAlerte(Long id) {
        if (!alerteRepository.existsById(id)) {
            throw new EntityNotFoundException("Alerte non trouvée avec l'ID: " + id);
        }
        alerteRepository.deleteById(id);
    }

    public AlerteDto toDto(Alerte alerte) {
        AlerteDto dto = new AlerteDto();
        dto.setId(alerte.getId());
        
        if (alerte.getProjet() != null) {
            dto.setProjetId(alerte.getProjet().getId());
            dto.setProjetNom(alerte.getProjet().getNom());
        }
        
        if (alerte.getEtape() != null) {
            dto.setEtapeId(alerte.getEtape().getId());
            dto.setEtapeNom(alerte.getEtape().getNom());
        }
        
        dto.setType(alerte.getType());
        dto.setNiveau(alerte.getNiveau());
        dto.setMessage(alerte.getMessage());
        dto.setLue(alerte.getLue());
        dto.setResolue(alerte.getResolue());
        
        if (alerte.getDestinataire() != null) {
            dto.setDestinataireId(alerte.getDestinataire().getId());
        }
        
        dto.setCreatedAt(alerte.getCreatedAt());
        
        return dto;
    }
}
