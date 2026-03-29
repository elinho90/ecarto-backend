package com.gs2e.stage_eranove_academy.entite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntiteDto {
    private Long id;
    private String code;
    private String nom;
    private String couleurTheme;
}
