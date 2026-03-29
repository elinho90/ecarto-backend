package com.gs2e.stage_eranove_academy.comite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComiteDto {
    private Long id;
    private String code;
    private String nom;
    private String description;
    private Long presidentId;
    private String presidentNom;
}
