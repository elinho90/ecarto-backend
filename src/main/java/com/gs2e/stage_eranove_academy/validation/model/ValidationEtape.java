package com.gs2e.stage_eranove_academy.validation.model;

import com.gs2e.stage_eranove_academy.etape.model.Etape;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "validations_etapes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationEtape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etape_id", nullable = false)
    private Etape etape;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validateur_id", nullable = false)
    private Utilisateur validateur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DecisionValidation decision;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "date_validation", nullable = false)
    private LocalDateTime dateValidation;

    @Column(name = "pieces_jointes", columnDefinition = "TEXT")
    private String piecesJointes;
}
