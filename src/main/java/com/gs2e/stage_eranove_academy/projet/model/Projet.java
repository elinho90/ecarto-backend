package com.gs2e.stage_eranove_academy.projet.model;

import com.gs2e.stage_eranove_academy.common.model.AuditModel;
import com.gs2e.stage_eranove_academy.site.model.Site;
import com.gs2e.stage_eranove_academy.typeprojet.model.TypeProjet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projets")
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Projet extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutProjet statut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioriteProjet priorite;

    @Column(nullable = false)
    private String responsable;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin_prevue")
    private LocalDate dateFinPrevue;

    @Column(name = "date_fin_reelle")
    private LocalDate dateFinReelle;

    @Column(precision = 12, scale = 2)
    private BigDecimal budget;

    @Column(nullable = false)
    private Integer progression = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_projet_id")
    private TypeProjet typeProjet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    @ElementCollection
    @CollectionTable(name = "projet_equipe", joinColumns = @JoinColumn(name = "projet_id"))
    @Column(name = "membre")
    private Set<String> equipe = new HashSet<>();

    @Column(name = "tags")
    private String tags;

    public enum StatutProjet {
        PREVU("À Venir"),
        EN_COURS("En Cours"),
        TERMINE("Terminé"),
        ANNULE("Annulé");

        private final String libelle;

        StatutProjet(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum PrioriteProjet {
        FAIBLE("Basse"),
        MOYENNE("Moyenne"),
        HAUTE("Haute"),
        CRITIQUE("Critique");

        private final String libelle;

        PrioriteProjet(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }
}