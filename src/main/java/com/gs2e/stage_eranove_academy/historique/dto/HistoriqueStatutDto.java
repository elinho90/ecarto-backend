package com.gs2e.stage_eranove_academy.historique.dto;

import com.gs2e.stage_eranove_academy.projet.model.Projet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueStatutDto {
    private Long id;
    private Long projetId;
    private String projetNom;
    private Projet.StatutProjet statutAvant;
    private Projet.StatutProjet statutApres;
    private Long utilisateurId;
    private String utilisateurNom;
    private String motif;
    private LocalDateTime dateChangement;
}
