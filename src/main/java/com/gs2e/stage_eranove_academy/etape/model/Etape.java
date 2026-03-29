package com.gs2e.stage_eranove_academy.etape.model;

import com.gs2e.stage_eranove_academy.common.model.AuditModel;
import com.gs2e.stage_eranove_academy.phase.model.Phase;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "etapes")
@Data
@EqualsAndHashCode(callSuper = false, exclude = {"phase"})
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class Etape extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer ordre = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_id", nullable = false)
    private Phase phase;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Utilisateur responsable;

    @Column(name = "date_echeance", nullable = false)
    private LocalDate dateEcheance;

    @Column(name = "date_realisation")
    private LocalDate dateRealisation;

    @Column(name = "duree_estimee_jours")
    private Integer dureeEstimeeJours;

    @Column(name = "duree_reelle_jours")
    private Integer dureeReelleJours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutEtape statut = StatutEtape.A_FAIRE;

    @Column(name = "validation_requise", nullable = false)
    private Boolean validationRequise = true;

    @Column(nullable = false)
    private Boolean bloquante = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_livrable", length = 30)
    private TypeLivrable typeLivrable = TypeLivrable.AUCUN;

    @Column(name = "url_livrable", columnDefinition = "TEXT")
    private String urlLivrable;

    /**
     * Vérifie si cette étape est en retard
     */
    @Transient
    public boolean isEnRetard() {
        return statut != StatutEtape.VALIDEE
                && dateEcheance != null
                && LocalDate.now().isAfter(dateEcheance);
    }

    /**
     * Nombre de jours de retard
     */
    @Transient
    public long getJoursRetard() {
        if (!isEnRetard()) return 0;
        return ChronoUnit.DAYS.between(dateEcheance, LocalDate.now());
    }

    /**
     * Nombre de jours restants avant l'échéance
     */
    @Transient
    public long getJoursRestants() {
        if (dateEcheance == null || statut == StatutEtape.VALIDEE) return 0;
        long jours = ChronoUnit.DAYS.between(LocalDate.now(), dateEcheance);
        return Math.max(0, jours);
    }
}
