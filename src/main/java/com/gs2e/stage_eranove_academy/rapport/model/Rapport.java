package com.gs2e.stage_eranove_academy.rapport.model;

import com.gs2e.stage_eranove_academy.common.model.AuditModel;
import com.gs2e.stage_eranove_academy.projet.model.Projet;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rapports",
        indexes = {
                @Index(name = "idx_rapport_projet_id", columnList = "projet_id"),
                @Index(name = "idx_rapport_upload_date", columnList = "created_at"),
                @Index(name = "idx_rapport_risque", columnList = "risque"),
                @Index(name = "idx_rapport_faisabilite", columnList = "faisabilite")
        })
@Getter
@Setter
@ToString(exclude = {"projet"})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rapport extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 255)
    private String fichierNom;

    @Column(nullable = false, length = 20)
    private String fichierType;

    @Column(nullable = false)
    private Long fichierTaille;

    @Column(nullable = false, length = 500)
    private String fichierChemin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id", foreignKey = @ForeignKey(name = "fk_rapport_projet"))
    private Projet projet;

    @Column(nullable = false, length = 100)
    private String uploadePar;

    @Column(nullable = false)
    private Integer faisabilite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NiveauRisque risque;

    @Column(precision = 12, scale = 2)
    private BigDecimal budgetEstime;

    @Column(nullable = false)
    private Integer dureeEstimeeMois;

    @Column(columnDefinition = "TEXT")
    private String recommandations;

    @Column(nullable = false)
    @Builder.Default
    private Boolean analyseAutomatique = false;

    @Version
    private Long version;

    public enum NiveauRisque {
        FAIBLE("Faible"),
        MOYEN("Moyen"),
        ELEVE("Élevé"),
        CRITIQUE("Critique");

        private final String libelle;

        NiveauRisque(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }
}