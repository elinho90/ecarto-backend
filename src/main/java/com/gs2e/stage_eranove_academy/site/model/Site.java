package com.gs2e.stage_eranove_academy.site.model;

import com.gs2e.stage_eranove_academy.common.model.AuditModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sites")
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Site extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false)
    private String ville;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String pays = "Côte d'Ivoire";

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeSite type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatutSite statut = StatutSite.ACTIF;

    private String contactPersonne;
    private String contactTelephone;
    private String contactEmail;

    private Integer nombreEmployes;
    private String horairesOuverture;

    @Column(columnDefinition = "TEXT")
    private String equipements;

    public enum TypeSite {
        SIEGE_SOCIAL,
        BUREAU_REGIONAL,
        CENTRE_OPERATIONNEL,
        DATACENTER,
        SITE_CLIENT,
        FORMATION
    }

    public enum StatutSite {
        ACTIF,
        INACTIF,
        EN_CONSTRUCTION,
        EN_MAINTENANCE
    }
}