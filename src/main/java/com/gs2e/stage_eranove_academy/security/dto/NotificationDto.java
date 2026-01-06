package com.gs2e.stage_eranove_academy.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private Long utilisateurId;
    private String type;
    private String titre;
    private String message;
    private Boolean lu;
    private LocalDateTime createdAt;
}
