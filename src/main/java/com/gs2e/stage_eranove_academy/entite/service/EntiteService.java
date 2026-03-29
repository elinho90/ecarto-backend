package com.gs2e.stage_eranove_academy.entite.service;

import com.gs2e.stage_eranove_academy.entite.dto.EntiteDto;
import com.gs2e.stage_eranove_academy.entite.model.Entite;
import com.gs2e.stage_eranove_academy.entite.repository.EntiteRepository;
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
public class EntiteService {

    private final EntiteRepository entiteRepository;

    @Transactional(readOnly = true)
    public List<EntiteDto> getAllEntites() {
        return entiteRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EntiteDto getEntiteById(Long id) {
        Entite entite = entiteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entité non trouvée avec l'ID: " + id));
        return toDto(entite);
    }

    private EntiteDto toDto(Entite entite) {
        EntiteDto dto = new EntiteDto();
        dto.setId(entite.getId());
        dto.setCode(entite.getCode());
        dto.setNom(entite.getNom());
        dto.setCouleurTheme(entite.getCouleurTheme());
        return dto;
    }
}
