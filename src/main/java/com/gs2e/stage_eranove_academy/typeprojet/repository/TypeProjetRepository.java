package com.gs2e.stage_eranove_academy.typeprojet.repository;

import com.gs2e.stage_eranove_academy.typeprojet.model.TypeProjet;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TypeProjetRepository extends JpaRepository<TypeProjet, Long> {
    @Query(value = "SELECT * FROM type_projet WHERE nom = :nom", nativeQuery = true)
    java.util.Optional<TypeProjet> findByNomNative(@Param("nom") String nom);
}
