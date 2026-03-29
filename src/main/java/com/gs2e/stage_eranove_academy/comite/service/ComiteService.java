package com.gs2e.stage_eranove_academy.comite.service;

import com.gs2e.stage_eranove_academy.comite.dto.ComiteDto;
import com.gs2e.stage_eranove_academy.comite.model.Comite;
import com.gs2e.stage_eranove_academy.comite.repository.ComiteRepository;
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
public class ComiteService {

    private final ComiteRepository comiteRepository;

    @Transactional(readOnly = true)
    public List<ComiteDto> getAllComites() {
        return comiteRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ComiteDto getComiteById(Long id) {
        Comite comite = comiteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comité non trouvé avec l'ID: " + id));
        return toDto(comite);
    }

    @Transactional
    public ComiteDto createComite(ComiteDto dto) {
        Comite comite = new Comite();
        comite.setCode(dto.getCode());
        comite.setNom(dto.getNom());
        comite.setDescription(dto.getDescription());
        
        Comite saved = comiteRepository.save(comite);
        return toDto(saved);
    }

    private ComiteDto toDto(Comite comite) {
        ComiteDto dto = new ComiteDto();
        dto.setId(comite.getId());
        dto.setCode(comite.getCode());
        dto.setNom(comite.getNom());
        dto.setDescription(comite.getDescription());
        if (comite.getPresident() != null) {
            dto.setPresidentId(comite.getPresident().getId());
            dto.setPresidentNom(comite.getPresident().getNom() + " " + comite.getPresident().getPrenom());
        }
        return dto;
    }
}
