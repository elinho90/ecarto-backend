package com.gs2e.stage_eranove_academy.historique.model;

import com.gs2e.stage_eranove_academy.projet.model.Projet;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_statuts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueStatut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id", nullable = false)
    private Projet projet;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_avant", length = 30)
    private Projet.StatutProjet statutAvant;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_apres", nullable = false, length = 30)
    private Projet.StatutProjet statutApres;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(columnDefinition = "TEXT")
    private String motif;

    @CreationTimestamp
    @Column(name = "date_changement", nullable = false)
    private LocalDateTime dateChangement;
}
