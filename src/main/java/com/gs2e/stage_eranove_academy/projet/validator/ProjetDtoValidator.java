package com.gs2e.stage_eranove_academy.projet.validator;

import com.gs2e.stage_eranove_academy.common.Exceptions.InvalidOperationException;
import com.gs2e.stage_eranove_academy.projet.dto.ProjetDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal; // ✅ AJOUTÉ

@Component
@Slf4j
public class ProjetDtoValidator {

    public void validate(ProjetDto projetDto) {
        if (projetDto == null) {
            throw new InvalidOperationException("Les données du projet ne peuvent pas être nulles");
        }

        // Validation métier supplémentaire
        if (projetDto.getBudget() != null) {
            validateBudgetRange(projetDto.getBudget());
        }
    }

    private void validateBudgetRange(BigDecimal budget) {
        if (budget.compareTo(BigDecimal.ZERO) <= 0) { // ✅ CORRIGÉ : BigDecimal ajouté
            throw new InvalidOperationException("Le budget doit être supérieur à 0");
        }
        if (budget.compareTo(new BigDecimal("10000000")) > 0) { // ✅ CORRIGÉ : BigDecimal ajouté
            throw new InvalidOperationException("Le budget ne peut pas dépasser 10 millions");
        }
    }
}