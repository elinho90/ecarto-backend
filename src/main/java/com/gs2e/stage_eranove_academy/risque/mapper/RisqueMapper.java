package com.gs2e.stage_eranove_academy.risque.mapper;

import com.gs2e.stage_eranove_academy.risque.dto.RisqueDto;
import com.gs2e.stage_eranove_academy.risque.model.Risque;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RisqueMapper {

    @Mapping(target = "projetId", source = "projet.id")
    @Mapping(target = "responsableId", source = "responsable.id")
    @Mapping(target = "responsableNom", expression = "java(risque.getResponsable() != null ? risque.getResponsable().getPrenom() + \" \" + risque.getResponsable().getNom() : null)")
    RisqueDto toDto(Risque risque);

    List<RisqueDto> toDtoList(List<Risque> risques);
}

