package com.gs2e.stage_eranove_academy.alerte.dto;

import com.gs2e.stage_eranove_academy.alerte.model.NiveauAlerte;
import com.gs2e.stage_eranove_academy.alerte.model.TypeAlerte;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlerteDto {
    private Long id;
    private Long projetId;
    private String projetNom;
    private Long etapeId;
    private String etapeNom;
    private TypeAlerte type;
    private NiveauAlerte niveau;
    private String message;
    private Boolean lue;
    private Boolean resolue;
    private Long destinataireId;
    private LocalDateTime createdAt;
}
