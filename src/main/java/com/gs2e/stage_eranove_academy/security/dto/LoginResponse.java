package com.gs2e.stage_eranove_academy.security.dto;

import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private UserInfo user; // Seul ce champ existe

    /**
     * DTO imbriqué pour les infos utilisateur
     */
    @Data
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String email;
        private String nom;
        private String prenom;
        private String role;
        private String departement;
        private String poste;
    }

    /**
     * Helper pour convertir automatiquement Utilisateur → UserInfo
     * ✅ Cette méthode peut rester si vous voulez un helper
     */
    public void setUserFromEntity(Utilisateur utilisateur) {
        if (utilisateur != null) {
            this.user = new UserInfo(
                    utilisateur.getId(),
                    utilisateur.getEmail(),
                    utilisateur.getNom(),
                    utilisateur.getPrenom(),
                    utilisateur.getRole() != null ? utilisateur.getRole().name() : null,
                    utilisateur.getDepartement(),
                    utilisateur.getPoste()
            );
        }
    }
}