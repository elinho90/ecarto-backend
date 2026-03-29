package com.gs2e.stage_eranove_academy.risque.model;

import com.gs2e.stage_eranove_academy.projet.model.Projet;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDate;

@Entity
@Table(name = "risques")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class Risque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id", nullable = false)
    private Projet projet;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NiveauRisque probabilite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NiveauRisque impact;

    @Column(columnDefinition = "TEXT", name = "plan_mitigation")
    private String planMitigation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutRisque statut = StatutRisque.IDENTIFIE;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Utilisateur responsable;

    @Column(name = "date_identification", nullable = false)
    private LocalDate dateIdentification = LocalDate.now();

    @Column(name = "date_resolution")
    private LocalDate dateResolution;
}
