package com.gs2e.stage_eranove_academy.comite.repository;

import com.gs2e.stage_eranove_academy.comite.model.Comite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComiteRepository extends JpaRepository<Comite, Long> {
    Optional<Comite> findByCode(String code);
}
