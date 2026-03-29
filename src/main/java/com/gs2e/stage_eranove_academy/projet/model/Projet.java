package com.gs2e.stage_eranove_academy.projet.model;

import com.gs2e.stage_eranove_academy.common.model.AuditModel;
import com.gs2e.stage_eranove_academy.comite.model.Comite;
import com.gs2e.stage_eranove_academy.entite.model.Entite;
import com.gs2e.stage_eranove_academy.phase.model.Phase;
import com.gs2e.stage_eranove_academy.site.model.Site;
import com.gs2e.stage_eranove_academy.typeprojet.model.TypeProjet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "projets")
@EqualsAndHashCode(callSuper = false, exclude = {"phases"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class Projet extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
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

    @Column(name = "budget_consomme", precision = 12, scale = 2)
    private BigDecimal budgetConsomme = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer progression = 0;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_projet_id")
    private TypeProjet typeProjet;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comite_id")
    private Comite comite;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entite_id")
    private Entite entite;

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<Phase> phases = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "projet_equipe", joinColumns = @JoinColumn(name = "projet_id"))
    @Column(name = "membre")
    private Set<String> equipe = new HashSet<>();

    @Column(name = "tags")
    private String tags;

    // =============================================
    // 11 statuts reflétant le cycle de vie réel GS2E
    // =============================================
    public enum StatutProjet {
        IDEE("Idée"),
        CADRAGE("Cadrage"),
        ETUDE_FAISABILITE("Étude de Faisabilité"),
        VALIDE("Validé"),
        EN_COURS("En Cours"),
        EN_PAUSE("En Pause"),
        RECETTE("Recette"),
        DEPLOIEMENT("Déploiement"),
        EN_PRODUCTION("En Production"),
        CLOTURE("Clôturé"),
        REJETE("Rejeté"),
        // Legacy statuts conservés pour compatibilité base de données existante
        PREVU("À Venir"),
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