package com.gs2e.stage_eranove_academy.risque.repository;

import com.gs2e.stage_eranove_academy.risque.model.Risque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RisqueRepository extends JpaRepository<Risque, Long> {
    List<Risque> findByProjetId(Long projetId);
}
