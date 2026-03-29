package com.gs2e.stage_eranove_academy.validation.service;

import com.gs2e.stage_eranove_academy.validation.dto.ValidationEtapeDto;
import com.gs2e.stage_eranove_academy.validation.model.ValidationEtape;
import com.gs2e.stage_eranove_academy.validation.repository.ValidationEtapeRepository;
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
public class ValidationService {

    private final ValidationEtapeRepository validationEtapeRepository;

    @Transactional(readOnly = true)
    public List<ValidationEtapeDto> getValidationsByEtape(Long etapeId) {
        return validationEtapeRepository.findByEtapeIdOrderByDateValidationDesc(etapeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ValidationEtapeDto getValidationById(Long id) {
        ValidationEtape validation = validationEtapeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Validation non trouvée avec l'ID: " + id));
        return toDto(validation);
    }

    public ValidationEtapeDto toDto(ValidationEtape validation) {
        ValidationEtapeDto dto = new ValidationEtapeDto();
        dto.setId(validation.getId());
        
        if (validation.getEtape() != null) {
            dto.setEtapeId(validation.getEtape().getId());
            dto.setEtapeNom(validation.getEtape().getNom());
        }
        
        if (validation.getValidateur() != null) {
            dto.setValidateurId(validation.getValidateur().getId());
            dto.setValidateurNom(validation.getValidateur().getNom() + " " + validation.getValidateur().getPrenom());
        }
        
        dto.setDecision(validation.getDecision());
        dto.setCommentaire(validation.getCommentaire());
        dto.setDateValidation(validation.getDateValidation());
        dto.setPiecesJointes(validation.getPiecesJointes());
        
        return dto;
    }
}
