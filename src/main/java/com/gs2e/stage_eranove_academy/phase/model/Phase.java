package com.gs2e.stage_eranove_academy.phase.model;

import com.gs2e.stage_eranove_academy.common.model.AuditModel;
import com.gs2e.stage_eranove_academy.etape.model.Etape;
import com.gs2e.stage_eranove_academy.projet.model.Projet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "phases")
@Data
@EqualsAndHashCode(callSuper = false, exclude = {"etapes", "projet"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class Phase extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer ordre = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id", nullable = false)
    private Projet projet;

    @Column(name = "date_debut_prevue")
    private LocalDate dateDebutPrevue;

    @Column(name = "date_fin_prevue")
    private LocalDate dateFinPrevue;

    @Column(name = "date_debut_reelle")
    private LocalDate dateDebutReelle;

    @Column(name = "date_fin_reelle")
    private LocalDate dateFinReelle;

    @Column(nullable = false)
    private Integer progression = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutPhase statut = StatutPhase.A_VENIR;

    @Column(nullable = false)
    private Boolean verrouillee = false;

    @OneToMany(mappedBy = "phase", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<Etape> etapes = new ArrayList<>();

    /**
     * Recalcule la progression de la phase en fonction des étapes validées.
     */
    public void recalculerProgression() {
        if (etapes == null || etapes.isEmpty()) {
            this.progression = 0;
            return;
        }
        long total = etapes.size();
        long terminees = etapes.stream()
                .filter(e -> e.getStatut() == com.gs2e.stage_eranove_academy.etape.model.StatutEtape.VALIDEE)
                .count();
        this.progression = (int) ((terminees * 100) / total);

        if (progression == 100) {
            this.statut = StatutPhase.TERMINEE;
            if (this.dateFinReelle == null) {
                this.dateFinReelle = LocalDate.now();
            }
        } else if (progression > 0) {
            this.statut = StatutPhase.EN_COURS;
            if (this.dateDebutReelle == null) {
                this.dateDebutReelle = LocalDate.now();
            }
        }
    }
}
