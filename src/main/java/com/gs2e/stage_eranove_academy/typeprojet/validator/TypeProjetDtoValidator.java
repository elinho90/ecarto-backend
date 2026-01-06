package com.gs2e.stage_eranove_academy.typeprojet.validator;

import com.gs2e.stage_eranove_academy.typeprojet.dto.TypeProjetDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TypeProjetDtoValidator {

    public static List<String> validate(TypeProjetDto dto) {

        List<String> errors = new ArrayList<>();

        if (dto == null) {
            errors.add("Veuillez renseigner le libelle du type-projet");
            return errors;
        }

        if (!StringUtils.hasLength(dto.getNom())) {
            errors.add("Veuillez renseigner le libelle du typeprojet");
        }

        return errors;
    }

}
