package com.gs2e.stage_eranove_academy.validation.repository;

import com.gs2e.stage_eranove_academy.validation.model.ValidationEtape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValidationEtapeRepository extends JpaRepository<ValidationEtape, Long> {
    List<ValidationEtape> findByEtapeIdOrderByDateValidationDesc(Long etapeId);
    long countByEtapeId(Long etapeId);
}
