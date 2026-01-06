package com.gs2e.stage_eranove_academy.typeprojet.mapper;

import com.gs2e.stage_eranove_academy.typeprojet.dto.TypeProjetDto;
import com.gs2e.stage_eranove_academy.typeprojet.model.TypeProjet;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TypeProjetMapper {

    TypeProjet toEntity(TypeProjetDto typeProjetDto);

    TypeProjetDto toDto(TypeProjet typeProjet);
}
