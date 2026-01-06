package com.gs2e.stage_eranove_academy.typeprojet.service;

import com.gs2e.stage_eranove_academy.typeprojet.dto.TypeProjetDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TypeProjetService {
    TypeProjetDto create(TypeProjetDto typeProjetDto);

    Page<TypeProjetDto> getAll(Pageable pageable);

    TypeProjetDto getById(Long id);

    void delete(Long id);
}
