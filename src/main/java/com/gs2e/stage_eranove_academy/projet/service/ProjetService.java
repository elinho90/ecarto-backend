package com.gs2e.stage_eranove_academy.projet.service;

import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.common.Exceptions.InvalidOperationException;
import com.gs2e.stage_eranove_academy.projet.dto.ProjetDto;
import com.gs2e.stage_eranove_academy.projet.model.Projet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ProjetService {

    Page<ProjetDto> getAllProjects(Pageable pageable);

    ProjetDto getProjectById(Long id) throws EntityNotFoundException;

    ProjetDto createProject(ProjetDto projetDto) throws InvalidOperationException;

    ProjetDto updateProject(Long id, ProjetDto projetDto) throws EntityNotFoundException, InvalidOperationException;

    void deleteProject(Long id) throws EntityNotFoundException;

    Page<ProjetDto> searchProjects(String nom, Projet.StatutProjet statut, String responsable,
                                   Long typeProjetId, LocalDate dateDebutFrom, LocalDate dateDebutTo,
                                   BigDecimal budgetMin, BigDecimal budgetMax, Pageable pageable);

    Page<ProjetDto> getProjectsByStatus(Projet.StatutProjet status, Pageable pageable);

    Page<ProjetDto> getProjectsByResponsable(String responsable, Pageable pageable);

    Page<ProjetDto> getProjectsByTypeProjet(Long typeProjetId, Pageable pageable);

    List<ProjetDto> getDelayedProjects();

    Map<String, Object> getProjectStatistics();

    ProjetDto updateProjectStatus(Long id, Projet.StatutProjet newStatus) throws EntityNotFoundException;

    ProjetDto updateProjectProgress(Long id, Integer progress) throws EntityNotFoundException;

    Page<ProjetDto> getProjectsByTeamMember(String membre, Pageable pageable);

    List<ProjetDto> getProjectsEndingSoon(int days);

    Map<String, BigDecimal> getBudgetStatisticsByStatus();
}