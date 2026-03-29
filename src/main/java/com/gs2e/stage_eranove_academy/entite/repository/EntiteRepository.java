package com.gs2e.stage_eranove_academy.entite.repository;

import com.gs2e.stage_eranove_academy.entite.model.Entite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntiteRepository extends JpaRepository<Entite, Long> {
    Optional<Entite> findByCode(String code);
}
