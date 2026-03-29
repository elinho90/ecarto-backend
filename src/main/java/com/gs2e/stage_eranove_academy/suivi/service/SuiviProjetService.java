package com.gs2e.stage_eranove_academy.suivi.service;

import com.gs2e.stage_eranove_academy.alerte.dto.AlerteDto;
import com.gs2e.stage_eranove_academy.alerte.model.Alerte;
import com.gs2e.stage_eranove_academy.alerte.model.NiveauAlerte;
import com.gs2e.stage_eranove_academy.alerte.model.TypeAlerte;
import com.gs2e.stage_eranove_academy.alerte.repository.AlerteRepository;
import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.common.Exceptions.InvalidOperationException;
import com.gs2e.stage_eranove_academy.etape.dto.EtapeDto;
import com.gs2e.stage_eranove_academy.etape.model.Etape;
import com.gs2e.stage_eranove_academy.etape.model.StatutEtape;
import com.gs2e.stage_eranove_academy.etape.repository.EtapeRepository;
import com.gs2e.stage_eranove_academy.historique.model.HistoriqueStatut;
import com.gs2e.stage_eranove_academy.historique.repository.HistoriqueStatutRepository;
import com.gs2e.stage_eranove_academy.phase.model.Phase;
import com.gs2e.stage_eranove_academy.phase.model.StatutPhase;
import com.gs2e.stage_eranove_academy.phase.repository.PhaseRepository;
import com.gs2e.stage_eranove_academy.projet.model.Projet;
import com.gs2e.stage_eranove_academy.projet.repository.ProjetRepository;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import com.gs2e.stage_eranove_academy.security.repository.UtilisateurRepository;
import com.gs2e.stage_eranove_academy.security.service.NotificationService;
import com.gs2e.stage_eranove_academy.validation.model.DecisionValidation;
import com.gs2e.stage_eranove_academy.validation.model.ValidationEtape;
import com.gs2e.stage_eranove_academy.validation.repository.ValidationEtapeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SuiviProjetService {

    private final EtapeRepository etapeRepository;
    private final PhaseRepository phaseRepository;
    private final ProjetRepository projetRepository;
    private final ValidationEtapeRepository validationRepository;
    private final AlerteRepository alerteRepository;
    private final HistoriqueStatutRepository historiqueRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;
    private final WebSocketNotificationService wsNotificationService; // Nouveauté

    // ═══════════════════════════════════════════════
    // 1. SOUMISSION D'ÉTAPE POUR VALIDATION
    // ═══════════════════════════════════════════════
    public EtapeDto soumettreEtapePourValidation(Long etapeId, Long utilisateurId, String urlLivrable) {
        Etape etape = etapeRepository.findById(etapeId)
                .orElseThrow(() -> new EntityNotFoundException("Étape non trouvée avec l'ID: " + etapeId));

        // Vérifier que l'étape n'est pas déjà validée
        if (etape.getStatut() == StatutEtape.VALIDEE) {
            throw new InvalidOperationException("Cette étape est déjà validée");
        }

        // Vérifier que l'étape n'est pas bloquée
        if (etape.getStatut() == StatutEtape.BLOQUEE) {
            throw new InvalidOperationException("Cette étape est bloquée, veuillez d'abord terminer les étapes précédentes");
        }

        etape.setStatut(StatutEtape.EN_ATTENTE_VALIDATION);
        if (urlLivrable != null && !urlLivrable.isBlank()) {
            etape.setUrlLivrable(urlLivrable);
        }

        Etape saved = etapeRepository.save(etape);
        log.info("Étape '{}' soumise pour validation par l'utilisateur {}", etape.getNom(), utilisateurId);

        // Créer une alerte pour le chef de projet
        creerAlerte(
                etape.getPhase().getProjet(),
                etape,
                TypeAlerte.ETAPE_A_VALIDER,
                NiveauAlerte.INFORMATION,
                "L'étape \"" + etape.getNom() + "\" attend votre validation",
                null // sera envoyée au chef de projet
        );

        return mapEtapeToDto(saved);
    }

    // ═══════════════════════════════════════════════
    // 2. VALIDATION / REJET D'UNE ÉTAPE
    // ═══════════════════════════════════════════════
    public EtapeDto validerEtape(Long etapeId, Long validateurId, DecisionValidation decision, String commentaire) {
        Etape etape = etapeRepository.findById(etapeId)
                .orElseThrow(() -> new EntityNotFoundException("Étape non trouvée avec l'ID: " + etapeId));

        Utilisateur validateur = utilisateurRepository.findById(validateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + validateurId));

        // Enregistrer la validation
        ValidationEtape validation = new ValidationEtape();
        validation.setEtape(etape);
        validation.setValidateur(validateur);
        validation.setDecision(decision);
        validation.setCommentaire(commentaire);
        validation.setDateValidation(LocalDateTime.now());
        validationRepository.save(validation);

        log.info("Validation de l'étape '{}' par {} : {}", etape.getNom(), validateur.getNom(), decision);

        switch (decision) {
            case APPROUVEE -> {
                etape.setStatut(StatutEtape.VALIDEE);
                etape.setDateRealisation(LocalDate.now());

                // Calculer la durée réelle
                if (etape.getPhase().getDateDebutReelle() != null) {
                    etape.setDureeReelleJours(
                            (int) ChronoUnit.DAYS.between(etape.getPhase().getDateDebutReelle(), LocalDate.now()));
                }

                etapeRepository.save(etape);

                // Recalculer la progression de la phase
                Phase phase = etape.getPhase();
                phase.recalculerProgression();
                phaseRepository.save(phase);

                // Recalculer la progression globale du projet
                recalculerProgressionProjet(phase.getProjet().getId());

                // Débloquer l'étape suivante
                debloquerEtapeSuivante(etape);

                // Vérifier si la phase est complète
                if (phase.getProgression() == 100) {
                    debloquerPhaseSuivante(phase);
                    creerAlerte(phase.getProjet(), null, TypeAlerte.PHASE_TERMINEE,
                            NiveauAlerte.INFORMATION,
                            "La phase \"" + phase.getNom() + "\" est terminée à 100%", null);
                }

                // Notification au responsable de l'étape
                if (etape.getResponsable() != null) {
                    try {
                        notificationService.createNotification(
                                etape.getResponsable(), "ETAPE_VALIDEE", "Étape validée",
                                "L'étape \"" + etape.getNom() + "\" a été approuvée par " + validateur.getNom());
                    } catch (Exception e) {
                        log.warn("Impossible de créer notification: {}", e.getMessage());
                    }
                }
            }

            case REJETEE -> {
                etape.setStatut(StatutEtape.REJETEE);
                etapeRepository.save(etape);

                creerAlerte(etape.getPhase().getProjet(), etape, TypeAlerte.ETAPE_REJETEE,
                        NiveauAlerte.IMPORTANT,
                        "L'étape \"" + etape.getNom() + "\" a été rejetée : " + commentaire,
                        etape.getResponsable());
            }

            case DEMANDE_MODIFICATION -> {
                etape.setStatut(StatutEtape.EN_COURS);
                etapeRepository.save(etape);

                if (etape.getResponsable() != null) {
                    try {
                        notificationService.createNotification(
                                etape.getResponsable(), "MODIFICATION_DEMANDEE", "Modification requise",
                                "Modification requise pour \"" + etape.getNom() + "\" : " + commentaire);
                    } catch (Exception e) {
                        log.warn("Impossible de créer notification: {}", e.getMessage());
                    }
                }
            }
        }

        return mapEtapeToDto(etape);
    }

    // ═══════════════════════════════════════════════
    // 3. CALCUL AUTOMATIQUE DE LA PROGRESSION
    // ═══════════════════════════════════════════════
    public void recalculerProgressionProjet(Long projetId) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + projetId));

        List<Phase> phases = phaseRepository.findByProjetIdOrderByOrdre(projetId);
        if (phases.isEmpty()) {
            return;
        }

        int totalEtapes = 0;
        int etapesValidees = 0;

        for (Phase phase : phases) {
            List<Etape> etapes = etapeRepository.findByPhaseIdOrderByOrdre(phase.getId());
            totalEtapes += etapes.size();
            etapesValidees += etapes.stream()
                    .filter(e -> e.getStatut() == StatutEtape.VALIDEE)
                    .count();
        }

        int progression = totalEtapes > 0 ? (etapesValidees * 100) / totalEtapes : 0;
        projet.setProgression(progression);
        projetRepository.save(projet);

        log.info("Progression du projet '{}' recalculée: {}% ({}/{} étapes)",
                projet.getNom(), progression, etapesValidees, totalEtapes);

        // Envoyer la MAJ au WebSocket
        wsNotificationService.diffuserMiseAJourProjet(
                projetId, projet.getNom(), projet.getStatut().name(), progression);
    }

    // ═══════════════════════════════════════════════
    // 4. DÉTECTION AUTOMATIQUE DES RETARDS (CRON)
    // ═══════════════════════════════════════════════
    @Scheduled(cron = "0 0 8 * * *") // Tous les jours à 8h
    public void detecterRetards() {
        log.info("=== Détection automatique des retards ===");
        List<StatutEtape> excludedStatuts = List.of(StatutEtape.VALIDEE, StatutEtape.BLOQUEE);
        List<Etape> etapesEnRetard = etapeRepository.findEtapesEnRetard(excludedStatuts, LocalDate.now());

        for (Etape etape : etapesEnRetard) {
            long joursRetard = ChronoUnit.DAYS.between(etape.getDateEcheance(), LocalDate.now());

            // Marquer en retard si ce n'est pas déjà fait
            if (etape.getStatut() != StatutEtape.EN_RETARD
                    && etape.getStatut() != StatutEtape.EN_ATTENTE_VALIDATION) {
                etape.setStatut(StatutEtape.EN_RETARD);
                etapeRepository.save(etape);
            }

            // Alerte graduée selon le retard
            NiveauAlerte niveau;
            if (joursRetard <= 2) niveau = NiveauAlerte.AVERTISSEMENT;
            else if (joursRetard <= 5) niveau = NiveauAlerte.IMPORTANT;
            else if (joursRetard <= 10) niveau = NiveauAlerte.URGENT;
            else niveau = NiveauAlerte.CRITIQUE;

            String message = String.format("⚠️ L'étape \"%s\" a %d jour(s) de retard (échéance: %s)",
                    etape.getNom(), joursRetard, etape.getDateEcheance());

            creerAlerte(etape.getPhase().getProjet(), etape, TypeAlerte.RETARD_ETAPE, niveau,
                    message, etape.getResponsable());

            log.warn("Retard détecté: {} - {} jour(s) [{}]", etape.getNom(), joursRetard, niveau);
        }

        // Détecter les étapes proches de l'échéance (dans 3 jours)
        List<Etape> etapesProches = etapeRepository.findEtapesProchesEcheance(
                excludedStatuts, LocalDate.now(), LocalDate.now().plusDays(3));

        for (Etape etape : etapesProches) {
            long joursRestants = ChronoUnit.DAYS.between(LocalDate.now(), etape.getDateEcheance());
            String message = String.format("📅 L'étape \"%s\" arrive à échéance dans %d jour(s)",
                    etape.getNom(), joursRestants);
            creerAlerte(etape.getPhase().getProjet(), etape, TypeAlerte.JALON_PROCHE,
                    NiveauAlerte.AVERTISSEMENT, message, etape.getResponsable());
        }

        log.info("=== Fin de la détection: {} retard(s), {} échéance(s) proche(s) ===",
                etapesEnRetard.size(), etapesProches.size());
    }

    // ═══════════════════════════════════════════════
    // 5. VÉRIFICATION AVANT TRANSITION STATUT PROJET
    // ═══════════════════════════════════════════════
    public boolean peutPasserEnRecette(Long projetId) {
        List<Etape> etapesBloquantes = etapeRepository.findByPhaseProjetIdAndBloquanteTrue(projetId);
        return etapesBloquantes.stream()
                .allMatch(e -> e.getStatut() == StatutEtape.VALIDEE);
    }

    // ═══════════════════════════════════════════════
    // 6. CHANGEMENT DE STATUT PROJET AVEC HISTORIQUE
    // ═══════════════════════════════════════════════
    public Projet changerStatutProjet(Long projetId, Projet.StatutProjet nouveauStatut,
                                      Long utilisateurId, String motif) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + projetId));

        Projet.StatutProjet ancienStatut = projet.getStatut();

        // Vérifier la transition EN_COURS -> RECETTE
        if (nouveauStatut == Projet.StatutProjet.RECETTE && !peutPasserEnRecette(projetId)) {
            throw new InvalidOperationException(
                    "Impossible de passer en Recette : toutes les étapes bloquantes ne sont pas validées");
        }

        // Enregistrer l'historique
        HistoriqueStatut historique = new HistoriqueStatut();
        historique.setProjet(projet);
        historique.setStatutAvant(ancienStatut);
        historique.setStatutApres(nouveauStatut);
        historique.setMotif(motif);
        if (utilisateurId != null) {
            utilisateurRepository.findById(utilisateurId)
                    .ifPresent(historique::setUtilisateur);
        }
        historiqueRepository.save(historique);

        // Appliquer le changement
        projet.setStatut(nouveauStatut);

        // Si clôturé, mettre la date de fin réelle
        if (nouveauStatut == Projet.StatutProjet.CLOTURE || nouveauStatut == Projet.StatutProjet.TERMINE) {
            projet.setDateFinReelle(LocalDate.now());
            projet.setProgression(100);
        }

        Projet saved = projetRepository.save(projet);
        log.info("Statut du projet '{}' changé de {} à {} (motif: {})",
                projet.getNom(), ancienStatut, nouveauStatut, motif);

        // WebSocket : Alerte globale du changement de statut du projet
        wsNotificationService.diffuserMiseAJourProjet(
                projetId, projet.getNom(), nouveauStatut.name(), projet.getProgression());

        return saved;
    }

    // ═══════════════════════════════════════════════
    // 7. OBTENIR LE RÉSUMÉ DE SUIVI D'UN PROJET
    // ═══════════════════════════════════════════════
    @Transactional(readOnly = true)
    public Map<String, Object> getSuiviProjet(Long projetId) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + projetId));

        List<Phase> phases = phaseRepository.findByProjetIdOrderByOrdre(projetId);

        int totalEtapes = 0;
        int etapesValidees = 0;
        int etapesEnRetard = 0;
        int etapesEnAttente = 0;

        for (Phase phase : phases) {
            List<Etape> etapes = etapeRepository.findByPhaseIdOrderByOrdre(phase.getId());
            totalEtapes += etapes.size();
            for (Etape e : etapes) {
                if (e.getStatut() == StatutEtape.VALIDEE) etapesValidees++;
                if (e.isEnRetard()) etapesEnRetard++;
                if (e.getStatut() == StatutEtape.EN_ATTENTE_VALIDATION) etapesEnAttente++;
            }
        }

        List<Alerte> alertesActives = alerteRepository.findByProjetIdAndResolueFalseOrderByCreatedAtDesc(projetId);

        return Map.of(
                "projetId", projetId,
                "projetNom", projet.getNom(),
                "statut", projet.getStatut(),
                "progression", projet.getProgression(),
                "totalPhases", phases.size(),
                "totalEtapes", totalEtapes,
                "etapesValidees", etapesValidees,
                "etapesEnRetard", etapesEnRetard,
                "etapesEnAttente", etapesEnAttente,
                "alertesActives", alertesActives.size()
        );
    }

    // ═══════════════════════════════════════════════
    // MÉTHODES UTILITAIRES INTERNES
    // ═══════════════════════════════════════════════

    private void debloquerEtapeSuivante(Etape etapeValidee) {
        List<Etape> etapesPhase = etapeRepository.findByPhaseIdOrderByOrdre(etapeValidee.getPhase().getId());
        boolean trouveCourante = false;

        for (Etape e : etapesPhase) {
            if (e.getId().equals(etapeValidee.getId())) {
                trouveCourante = true;
                continue;
            }
            if (trouveCourante && e.getStatut() == StatutEtape.BLOQUEE) {
                e.setStatut(StatutEtape.A_FAIRE);
                etapeRepository.save(e);
                log.info("Étape '{}' débloquée suite à la validation de '{}'",
                        e.getNom(), etapeValidee.getNom());
                break;
            }
        }
    }

    private void debloquerPhaseSuivante(Phase phaseTerminee) {
        List<Phase> phases = phaseRepository.findByProjetIdOrderByOrdre(phaseTerminee.getProjet().getId());
        boolean trouveCourante = false;

        for (Phase p : phases) {
            if (p.getId().equals(phaseTerminee.getId())) {
                trouveCourante = true;
                continue;
            }
            if (trouveCourante && p.getVerrouillee()) {
                p.setVerrouillee(false);
                p.setStatut(StatutPhase.EN_COURS);
                p.setDateDebutReelle(LocalDate.now());
                phaseRepository.save(p);
                log.info("Phase '{}' débloquée suite à la complétion de '{}'",
                        p.getNom(), phaseTerminee.getNom());
                break;
            }
        }
    }

    private void creerAlerte(Projet projet, Etape etape, TypeAlerte type,
                             NiveauAlerte niveau, String message, Utilisateur destinataire) {
        Alerte alerte = new Alerte();
        alerte.setProjet(projet);
        alerte.setEtape(etape);
        alerte.setType(type);
        alerte.setNiveau(niveau);
        alerte.setMessage(message);
        alerte.setDestinataire(destinataire);
        Alerte saved = alerteRepository.save(alerte);

        // Envoyer l'alerte via WebSocket
        AlerteDto alerteDto = mapAlerteToDto(saved);
        if (destinataire != null) {
            // Notification ciblée
            wsNotificationService.envoyerAlerteUtilisateur(destinataire.getId(), alerteDto);
        } else if (projet != null) {
            // Notification globale sur le projet
            wsNotificationService.diffuserAlerteProjet(projet.getId(), alerteDto);
        }
    }

    private EtapeDto mapEtapeToDto(Etape etape) {
        EtapeDto dto = new EtapeDto();
        dto.setId(etape.getId());
        dto.setNom(etape.getNom());
        dto.setDescription(etape.getDescription());
        dto.setOrdre(etape.getOrdre());
        dto.setPhaseId(etape.getPhase().getId());
        dto.setPhaseNom(etape.getPhase().getNom());
        if (etape.getResponsable() != null) {
            dto.setResponsableId(etape.getResponsable().getId());
            dto.setResponsableNom(etape.getResponsable().getNom() + " " + etape.getResponsable().getPrenom());
        }
        dto.setDateEcheance(etape.getDateEcheance());
        dto.setDateRealisation(etape.getDateRealisation());
        dto.setDureeEstimeeJours(etape.getDureeEstimeeJours());
        dto.setDureeReelleJours(etape.getDureeReelleJours());
        dto.setStatut(etape.getStatut());
        dto.setValidationRequise(etape.getValidationRequise());
        dto.setBloquante(etape.getBloquante());
        dto.setTypeLivrable(etape.getTypeLivrable());
        dto.setUrlLivrable(etape.getUrlLivrable());
        dto.setEnRetard(etape.isEnRetard());
        dto.setJoursRetard(etape.getJoursRetard());
        dto.setJoursRestants(etape.getJoursRestants());
        return dto;
    }

    private AlerteDto mapAlerteToDto(Alerte alerte) {
        AlerteDto dto = new AlerteDto();
        dto.setId(alerte.getId());
        if (alerte.getProjet() != null) {
            dto.setProjetId(alerte.getProjet().getId());
            dto.setProjetNom(alerte.getProjet().getNom());
        }
        if (alerte.getEtape() != null) {
            dto.setEtapeId(alerte.getEtape().getId());
            dto.setEtapeNom(alerte.getEtape().getNom());
        }
        dto.setType(alerte.getType());
        dto.setNiveau(alerte.getNiveau());
        dto.setMessage(alerte.getMessage());
        dto.setLue(alerte.getLue());
        dto.setResolue(alerte.getResolue());
        if (alerte.getDestinataire() != null) {
            dto.setDestinataireId(alerte.getDestinataire().getId());
        }
        dto.setCreatedAt(alerte.getCreatedAt());
        return dto;
    }

    // ═══════════════════════════════════════════════════════════════
    // BILAN AUTOMATIQUE DE PHASE (RAPPORT AUTO)
    // ═══════════════════════════════════════════════════════════════

    /**
     * Génère automatiquement un bilan lorsqu'une phase atteint 100%.
     * Appelé automatiquement par recalculerProgressionProjet() quand une phase est terminée.
     */
    public Map<String, Object> genererBilanPhase(Long phaseId) {
        Phase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new EntityNotFoundException("Phase non trouvée: " + phaseId));

        List<Etape> etapes = etapeRepository.findByPhaseIdOrderByOrdre(phaseId);
        long totalEtapes = etapes.size();
        long etapesValidees = etapes.stream().filter(e -> e.getStatut() == StatutEtape.VALIDEE).count();
        long etapesEnRetard = etapes.stream().filter(Etape::isEnRetard).count();
        long etapesRejetees = etapes.stream().filter(e -> e.getStatut() == StatutEtape.REJETEE).count();

        // Calcul durée réelle moyenne des étapes
        double dureeMoyenneJours = etapes.stream()
                .filter(e -> e.getDureeReelleJours() != null && e.getDureeReelleJours() > 0)
                .mapToInt(Etape::getDureeReelleJours)
                .average()
                .orElse(0.0);

        // Durée totale de la phase
        long dureeTotaleJours = 0;
        if (phase.getDateDebutReelle() != null && phase.getDateFinReelle() != null) {
            dureeTotaleJours = ChronoUnit.DAYS.between(phase.getDateDebutReelle(), phase.getDateFinReelle());
        } else if (phase.getDateDebutPrevue() != null && phase.getDateFinPrevue() != null) {
            dureeTotaleJours = ChronoUnit.DAYS.between(phase.getDateDebutPrevue(), phase.getDateFinPrevue());
        }

        // Dépassement (jours réels vs prévus)
        long depassementJours = 0;
        if (phase.getDateFinReelle() != null && phase.getDateFinPrevue() != null) {
            depassementJours = ChronoUnit.DAYS.between(phase.getDateFinPrevue(), phase.getDateFinReelle());
        }

        // Nombre de validations totales sur la phase
        long nbValidations = etapes.stream()
                .mapToLong(e -> validationRepository.findByEtapeIdOrderByDateValidationDesc(e.getId()).size())
                .sum();

        String bilanMessage = String.format(
                "📊 BILAN PHASE \"%s\" — %d/%d étapes validées | %d en retard | Durée : %d jours | Dépassement : %s%d jour(s) | %d validations au total | Durée moyenne par étape : %.1f jours",
                phase.getNom(), etapesValidees, totalEtapes, etapesEnRetard,
                dureeTotaleJours, depassementJours > 0 ? "+" : "", depassementJours,
                nbValidations, dureeMoyenneJours
        );

        log.info(bilanMessage);

        // Créer une alerte de type BILAN
        Alerte alerteBilan = new Alerte();
        alerteBilan.setProjet(phase.getProjet());
        alerteBilan.setType(TypeAlerte.PROGRESSION_PROJET);
        alerteBilan.setNiveau(depassementJours > 5 ? NiveauAlerte.IMPORTANT : NiveauAlerte.INFORMATION);
        alerteBilan.setMessage(bilanMessage);
        alerteBilan.setLue(false);
        alerteBilan.setResolue(false);
        alerteRepository.save(alerteBilan);

        // Notification WebSocket
        try {
            AlerteDto alerteDto = mapAlerteToDto(alerteBilan);
            wsNotificationService.diffuserAlerteProjet(phase.getProjet().getId(), alerteDto);
        } catch (Exception e) {
            log.warn("Impossible de diffuser le bilan de phase via WebSocket", e);
        }

        return Map.of(
                "phaseId", phaseId,
                "phaseNom", phase.getNom(),
                "totalEtapes", totalEtapes,
                "etapesValidees", etapesValidees,
                "etapesEnRetard", etapesEnRetard,
                "etapesRejetees", etapesRejetees,
                "dureeTotaleJours", dureeTotaleJours,
                "depassementJours", depassementJours,
                "nbValidations", nbValidations,
                "dureeMoyenneEtapeJours", Math.round(dureeMoyenneJours * 10) / 10.0,
                "progression", phase.getProgression(),
                "bilanMessage", bilanMessage
        );
    }
}
