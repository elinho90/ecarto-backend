package com.gs2e.stage_eranove_academy.typeprojet.model;

import com.gs2e.stage_eranove_academy.common.model.AuditModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "type_projet")
@SQLDelete(sql = "UPDATE type_projet SET est_actif = false WHERE id_type_projet = ?") // Correction: id_type-projet ->
                                                                                      // id_type_projet
@SQLRestriction("est_actif = true")
@Setter
@Getter
public class TypeProjet extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_projet", nullable = false)
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "est_actif")
    private Boolean estActif;
}