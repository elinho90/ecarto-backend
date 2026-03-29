package com.gs2e.stage_eranove_academy.projet.service.impl;

import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.common.Exceptions.InvalidOperationException;
import com.gs2e.stage_eranove_academy.comite.model.Comite;
import com.gs2e.stage_eranove_academy.comite.repository.ComiteRepository;
import com.gs2e.stage_eranove_academy.entite.model.Entite;
import com.gs2e.stage_eranove_academy.entite.repository.EntiteRepository;
import com.gs2e.stage_eranove_academy.projet.dto.ProjetDto;
import com.gs2e.stage_eranove_academy.projet.mapper.ProjetMapper;
import com.gs2e.stage_eranove_academy.projet.model.Projet;
import com.gs2e.stage_eranove_academy.projet.repository.ProjetRepository;
import com.gs2e.stage_eranove_academy.projet.service.ProjetService;
import com.gs2e.stage_eranove_academy.projet.validator.ProjetDtoValidator;
import com.gs2e.stage_eranove_academy.site.model.Site;
import com.gs2e.stage_eranove_academy.site.repository.SiteRepository;
import com.gs2e.stage_eranove_academy.typeprojet.model.TypeProjet;
import com.gs2e.stage_eranove_academy.typeprojet.repository.TypeProjetRepository;
import com.gs2e.stage_eranove_academy.security.service.AuthService;
import com.gs2e.stage_eranove_academy.security.service.NotificationService;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.*;

@Service
@Transactional
@Slf4j
public class ProjetServiceImpl implements ProjetService {

    private final ProjetRepository projetRepository;
    private final TypeProjetRepository typeProjetRepository;
    private final SiteRepository siteRepository;
    private final ComiteRepository comiteRepository;
    private final EntiteRepository entiteRepository;
    private final ProjetMapper projetMapper;
    private final ProjetDtoValidator validator;
    private final NotificationService notificationService;
    private final AuthService authService;

    @Autowired
    public ProjetServiceImpl(ProjetRepository projetRepository,
            TypeProjetRepository typeProjetRepository,
            SiteRepository siteRepository,
            ComiteRepository comiteRepository,
            EntiteRepository entiteRepository,
            ProjetMapper projetMapper,
            ProjetDtoValidator validator,
            NotificationService notificationService,
            AuthService authService) {
        this.projetRepository = projetRepository;
        this.typeProjetRepository = typeProjetRepository;
        this.siteRepository = siteRepository;
        this.comiteRepository = comiteRepository;
        this.entiteRepository = entiteRepository;
        this.projetMapper = projetMapper;
        this.validator = validator;
        this.notificationService = notificationService;
        this.authService = authService;
    }

    @Override
    public Page<ProjetDto> getAllProjects(Pageable pageable) {
        log.info("Récupération de tous les projets avec pagination");
        return projetRepository.findAll(pageable)
                .map(projetMapper::toDto);
    }

    @Override
    public ProjetDto getProjectById(Long id) throws EntityNotFoundException {
        log.info("Récupération du projet avec l'ID: {}", id);
        return projetRepository.findById(id)
                .map(projetMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + id));
    }

    @Override
    @CacheEvict(value = {"projets_stats", "projets_evolution", "projets_type_stats"}, allEntries = true)
    public ProjetDto createProject(ProjetDto projetDto) throws InvalidOperationException {
        log.info("Création d'un nouveau projet: {}", projetDto.getNom());

        // Validation
        validator.validate(projetDto);

        // Mapper en entité
        Projet projet = projetMapper.toEntity(projetDto);

        // Vérifier le site
        if (projetDto.getSiteId() != null) {
            Site site = siteRepository.findById(projetDto.getSiteId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Site non trouvé avec l'ID: " + projetDto.getSiteId()));
            projet.setSite(site);
        }

        // Gérer le type de projet
        if (projetDto.getTypeProjetId() != null) {
            TypeProjet typeProjet = typeProjetRepository.findById(projetDto.getTypeProjetId())
                    .orElseThrow(() -> new EntityNotFoundException("Type de projet non trouvé"));
            projet.setTypeProjet(typeProjet);
        }

        // Gérer le comité
        if (projetDto.getComiteId() != null) {
            Comite comite = comiteRepository.findById(projetDto.getComiteId())
                    .orElseThrow(() -> new EntityNotFoundException("Comité non trouvé"));
            projet.setComite(comite);
        }

        // Gérer l'entité
        if (projetDto.getEntiteId() != null) {
            Entite entite = entiteRepository.findById(projetDto.getEntiteId())
                    .orElseThrow(() -> new EntityNotFoundException("Entité non trouvée"));
            projet.setEntite(entite);
        }

        // Vérification anti-doublon: Nom + Site + Date Début
        if (projetDto.getSiteId() != null && projetRepository.existsByNomIgnoreCaseAndSiteIdAndDateDebut(
                projetDto.getNom(), projetDto.getSiteId(), projetDto.getDateDebut())) {
            throw new InvalidOperationException("Un projet avec ce nom sur ce site à cette date existe déjà.");
        }

        Projet savedProjet = projetRepository.save(projet);
        log.info("Projet créé avec succès avec l'ID: {}", savedProjet.getId());

        // Créer une notification pour le créateur
        try {
            Utilisateur currentUser = authService.getCurrentUser();
            notificationService.createNotification(
                    currentUser,
                    "PROJET_CREATION",
                    "Nouveau projet créé",
                    "Le projet '" + savedProjet.getNom() + "' a été créé avec succès.");
        } catch (Exception e) {
            log.warn("Impossible de créer la notification de création: {}", e.getMessage());
        }

        return projetMapper.toDto(savedProjet);
    }

    @Override
    @CacheEvict(value = {"projets_stats", "projets_evolution", "projets_type_stats"}, allEntries = true)
    public ProjetDto updateProject(Long id, ProjetDto projetDto)
            throws EntityNotFoundException, InvalidOperationException {
        log.info("Mise à jour du projet avec l'ID: {}", id);

        try {
            // Validation
            validator.validate(projetDto);

            Projet existingProjet = projetRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + id));

            log.debug("Projet existant trouvé: {}", existingProjet.getNom());

            // Vérifier l'unicité du nom (s'il a changé)
            if (!existingProjet.getNom().equals(projetDto.getNom()) &&
                    projetRepository.findByNomIgnoreCase(projetDto.getNom()).isPresent()) {
                throw new InvalidOperationException("Un projet avec ce nom existe déjà");
            }

            // Mettre à jour les champs
            log.debug("Mise à jour des champs basiques");
            existingProjet.setNom(projetDto.getNom());
            existingProjet.setDescription(projetDto.getDescription());
            existingProjet.setStatut(projetDto.getStatut());
            existingProjet.setPriorite(projetDto.getPriorite());
            existingProjet.setResponsable(projetDto.getResponsable());
            existingProjet.setDateDebut(projetDto.getDateDebut());
            existingProjet.setDateFinPrevue(projetDto.getDateFinPrevue());
            existingProjet.setDateFinReelle(projetDto.getDateFinReelle());
            existingProjet.setBudget(projetDto.getBudget());
            existingProjet.setBudgetConsomme(projetDto.getBudgetConsomme() != null ? projetDto.getBudgetConsomme() : BigDecimal.ZERO);
            existingProjet.setProgression(projetDto.getProgression());
            existingProjet.setEquipe(projetDto.getEquipe());
            existingProjet.setTags(projetDto.getTags());

            // Gérer le site
            log.debug("Gestion du site, siteId: {}", projetDto.getSiteId());
            if (projetDto.getSiteId() != null) {
                Site site = siteRepository.findById(projetDto.getSiteId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Site non trouvé avec l'ID: " + projetDto.getSiteId()));
                existingProjet.setSite(site);
                log.debug("Site associé: {}", site.getNom());
            } else {
                existingProjet.setSite(null);
                log.debug("Site défini à null");
            }

            // Gérer le type de projet
            log.debug("Gestion du type de projet, typeProjetId: {}", projetDto.getTypeProjetId());
            if (projetDto.getTypeProjetId() != null) {
                TypeProjet typeProjet = typeProjetRepository.findById(projetDto.getTypeProjetId())
                        .orElseThrow(() -> new EntityNotFoundException("Type de projet non trouvé"));
                existingProjet.setTypeProjet(typeProjet);
                log.debug("Type de projet associé: {}", typeProjet.getNom());
            } else {
                existingProjet.setTypeProjet(null);
                log.debug("Type de projet défini à null");
            }

            // Gérer le comité
            if (projetDto.getComiteId() != null) {
                Comite comite = comiteRepository.findById(projetDto.getComiteId())
                        .orElseThrow(() -> new EntityNotFoundException("Comité non trouvé"));
                existingProjet.setComite(comite);
            } else {
                existingProjet.setComite(null);
            }

            // Gérer l'entité
            if (projetDto.getEntiteId() != null) {
                Entite entite = entiteRepository.findById(projetDto.getEntiteId())
                        .orElseThrow(() -> new EntityNotFoundException("Entité non trouvée"));
                existingProjet.setEntite(entite);
            } else {
                existingProjet.setEntite(null);
            }

            log.debug("Sauvegarde du projet");
            Projet updatedProjet = projetRepository.save(existingProjet);
            log.info("Projet mis à jour avec succès");

            // Notification de mise à jour
            try {
                Utilisateur currentUser = authService.getCurrentUser();
                notificationService.createNotification(
                        currentUser,
                        "PROJET_UPDATE",
                        "Projet mis à jour",
                        "Le projet '" + updatedProjet.getNom() + "' a été mis à jour.");
            } catch (Exception e) {
                log.warn("Impossible de créer la notification de mise à jour: {}", e.getMessage());
            }

            return projetMapper.toDto(updatedProjet);

        } catch (EntityNotFoundException | InvalidOperationException e) {
            log.error("Erreur métier lors de la mise à jour du projet {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la mise à jour du projet {}", id, e);
            log.error("Type d'erreur: {}", e.getClass().getName());
            log.error("Message: {}", e.getMessage());
            log.error("Stack trace: ", e);
            throw new InvalidOperationException("Erreur lors de la mise à jour du projet: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = {"projets_stats", "projets_evolution", "projets_type_stats"}, allEntries = true)
    public void deleteProject(Long id) throws EntityNotFoundException {
        log.info("Suppression du projet avec l'ID: {}", id);

        if (!projetRepository.existsById(id)) {
            throw new EntityNotFoundException("Projet non trouvé avec l'ID: " + id);
        }

        Projet projet = projetRepository.findById(id).orElse(null);
        String projetNom = (projet != null) ? projet.getNom() : String.valueOf(id);

        projetRepository.deleteById(id);
        log.info("Projet supprimé avec succès");

        // Notification de suppression
        try {
            Utilisateur currentUser = authService.getCurrentUser();
            notificationService.createNotification(
                    currentUser,
                    "PROJET_DELETION",
                    "Projet supprimé",
                    "Le projet '" + projetNom + "' a été supprimé.");
        } catch (Exception e) {
            log.warn("Impossible de créer la notification de suppression: {}", e.getMessage());
        }
    }

    @Override
    public Page<ProjetDto> searchProjects(String nom, Projet.StatutProjet statut, String responsable,
            Long typeProjetId, LocalDate dateDebutFrom, LocalDate dateDebutTo,
            BigDecimal budgetMin, BigDecimal budgetMax, Pageable pageable) {
        log.info("Recherche de projets avec critères multiples");
        return projetRepository.searchProjects(nom, statut, responsable, typeProjetId,
                dateDebutFrom, dateDebutTo, budgetMin, budgetMax, pageable)
                .map(projetMapper::toDto);
    }

    @Override
    public Page<ProjetDto> getProjectsByStatus(Projet.StatutProjet status, Pageable pageable) {
        log.info("Récupération des projets avec le statut: {}", status);
        return projetRepository.findByStatut(status, pageable)
                .map(projetMapper::toDto);
    }

    @Override
    public Page<ProjetDto> getProjectsByResponsable(String responsable, Pageable pageable) {
        log.info("Récupération des projets du responsable: {}", responsable);
        return projetRepository.findByResponsableContainingIgnoreCase(responsable, pageable)
                .map(projetMapper::toDto);
    }

    @Override
    public Page<ProjetDto> getProjectsByTypeProjet(Long typeProjetId, Pageable pageable) {
        log.info("Récupération des projets du type: {}", typeProjetId);
        return projetRepository.findByTypeProjetId(typeProjetId, pageable)
                .map(projetMapper::toDto);
    }

    @Override
    public List<ProjetDto> getDelayedProjects() {
        log.info("Récupération des projets en retard");
        return projetRepository.findDelayedProjects().stream()
                .map(projetMapper::toDto)
                .toList();
    }

    @Override
    @Cacheable("projets_stats")
    public Map<String, Object> getProjectStatistics() {
        log.info("Calcul des statistiques des projets");
        Map<String, Object> stats = new HashMap<>();

        // Statistiques par statut
        Map<String, Long> countByStatus = new HashMap<>();
        projetRepository.countProjectsByStatut().forEach(result -> {
            Projet.StatutProjet status = (Projet.StatutProjet) result[0];
            Long count = (Long) result[1];
            countByStatus.put(status.name(), count);
        });
        stats.put("countByStatus", countByStatus);

        // Statistiques par priorité
        Map<String, Long> countByPriority = new HashMap<>();
        projetRepository.countProjectsByPriorite().forEach(result -> {
            Projet.PrioriteProjet priority = (Projet.PrioriteProjet) result[0];
            Long count = (Long) result[1];
            countByPriority.put(priority.name(), count);
        });
        stats.put("countByPriority", countByPriority);

        // Total des projets
        stats.put("totalProjects", projetRepository.count());

        // Projets en retard
        stats.put("delayedProjects", getDelayedProjects().size());

        // Budget total
        BigDecimal totalBudget = projetRepository.findAll().stream()
                .map(Projet::getBudget)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalBudget", totalBudget);

        // Comparaison temporelle (Ce mois vs mois dernier)
        LocalDate now = LocalDate.now();
        LocalDate startOfThisMonth = now.withDayOfMonth(1);
        LocalDate startOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
        LocalDate endOfLastMonth = startOfThisMonth.minusDays(1);

        long thisMonthCount = projetRepository.countByDateDebutBetween(startOfThisMonth, now);
        long lastMonthCount = projetRepository.countByDateDebutBetween(startOfLastMonth, endOfLastMonth);

        stats.put("thisMonthNewProjects", thisMonthCount);
        stats.put("lastMonthNewProjects", lastMonthCount);

        // Trend calculation
        if (lastMonthCount > 0) {
            double trend = ((double) (thisMonthCount - lastMonthCount) / lastMonthCount) * 100;
            stats.put("projectsTrend", trend);
        } else {
            stats.put("projectsTrend", thisMonthCount > 0 ? 100.0 : 0.0);
        }

        return stats;
    }

    @Override
    @Cacheable("projets_evolution")
    public List<Map<String, Object>> getProjectEvolution() {
        log.info("Récupération de l'évolution des projets");
        List<Map<String, Object>> evolution = new ArrayList<>();
        List<Object[]> results = projetRepository.getProjectEvolutionStats();

        for (Object[] row : results) {
            Map<String, Object> data = new HashMap<>();
            data.put("month", row[0]);
            data.put("year", row[1]);
            data.put("count", row[2]);
            data.put("budget", row[3]);
            evolution.add(data);
        }
        return evolution;
    }

    @Override
    @Cacheable("projets_type_stats")
    public Map<String, Long> getProjectsByTypeStats() {
        log.info("Récupération des statistiques par type de projet");
        Map<String, Long> stats = new HashMap<>();
        projetRepository.countProjectsByType().forEach(result -> {
            String typeName = (String) result[0];
            Long count = (Long) result[1];
            stats.put(typeName, count);
        });
        return stats;
    }

    @Override
    @CacheEvict(value = {"projets_stats", "projets_evolution", "projets_type_stats"}, allEntries = true)
    public ProjetDto updateProjectStatus(Long id, Projet.StatutProjet newStatus) throws EntityNotFoundException {
        log.info("Mise à jour du statut du projet {} vers {}", id, newStatus);

        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + id));

        projet.setStatut(newStatus);

        // Si le projet est terminé, mettre à jour la date de fin réelle
        if (newStatus == Projet.StatutProjet.TERMINE) {
            projet.setDateFinReelle(LocalDate.now());
            projet.setProgression(100);
        }

        Projet updatedProjet = projetRepository.save(projet);

        // Notification de mise à jour de statut
        try {
            Utilisateur currentUser = authService.getCurrentUser();
            notificationService.createNotification(
                    currentUser,
                    "PROJET_STATUS_UPDATE",
                    "Statut mis à jour",
                    "Le statut du projet '" + updatedProjet.getNom() + "' est passé à " + newStatus.getLibelle());
        } catch (Exception e) {
            log.warn("Impossible de créer la notification de statut: {}", e.getMessage());
        }

        return projetMapper.toDto(updatedProjet);
    }

    @Override
    @CacheEvict(value = {"projets_stats", "projets_evolution", "projets_type_stats"}, allEntries = true)
    public ProjetDto updateProjectProgress(Long id, Integer progress) throws EntityNotFoundException {
        log.info("Mise à jour de la progression du projet {} à {}", id, progress);

        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + id));

        projet.setProgression(progress);

        // Si la progression est à 100%, marquer le projet comme terminé
        if (progress == 100) {
            projet.setStatut(Projet.StatutProjet.TERMINE);
            projet.setDateFinReelle(LocalDate.now());
        }

        Projet updatedProjet = projetRepository.save(projet);
        return projetMapper.toDto(updatedProjet);
    }

    @Override
    public Page<ProjetDto> getProjectsByTeamMember(String membre, Pageable pageable) {
        log.info("Récupération des projets de l'équipe contenant: {}", membre);
        return projetRepository.findByEquipeContaining(membre, pageable)
                .map(projetMapper::toDto);
    }

    @Override
    public List<ProjetDto> getProjectsEndingSoon(int days) {
        log.info("Récupération des projets se terminant dans {} jours", days);
        LocalDate targetDate = LocalDate.now().plusDays(days);

        return projetRepository.findAll().stream()
                .filter(p -> p.getStatut() == Projet.StatutProjet.EN_COURS)
                .filter(p -> p.getDateFinPrevue() != null)
                .filter(p -> !p.getDateFinPrevue().isAfter(targetDate))
                .map(projetMapper::toDto)
                .toList();
    }

    @Override
    public Map<String, BigDecimal> getBudgetStatisticsByStatus() {
        log.info("Calcul des statistiques de budget par statut");
        Map<String, BigDecimal> budgetByStatus = new HashMap<>();

        projetRepository.getTotalBudgetByStatut().forEach(result -> {
            Projet.StatutProjet status = (Projet.StatutProjet) result[0];
            BigDecimal totalBudget = (BigDecimal) result[1];
            budgetByStatus.put(status.name(), totalBudget);
        });

        return budgetByStatus;
    }
}