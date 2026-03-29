package com.gs2e.stage_eranove_academy.suivi.service;

import com.gs2e.stage_eranove_academy.alerte.dto.AlerteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Diffuse un changement de statut de projet à tout le monde
     */
    public void diffuserMiseAJourProjet(Long projetId, String nom, String nouveauStatut, Integer progression) {
        log.info("WS: Diffusion maj projet {}", projetId);
        messagingTemplate.convertAndSend("/topic/projets", Map.of(
                "projetId", projetId,
                "nom", nom,
                "statut", nouveauStatut,
                "progression", progression
        ));
    }

    /**
     * Envoie une alerte générale (ex: sur la page du projet)
     */
    public void diffuserAlerteProjet(Long projetId, AlerteDto alerte) {
        log.info("WS: Diffusion alerte pour projet {}", projetId);
        messagingTemplate.convertAndSend("/topic/projets/" + projetId + "/alertes", alerte);
    }

    /**
     * Envoie une alerte spécifique à un utilisateur
     */
    public void envoyerAlerteUtilisateur(Long utilisateurId, AlerteDto alerte) {
        log.info("WS: Envoi alerte ciblée à l'utilisateur {}", utilisateurId);
        // Le client doit s'abonner à /user/{utilisateurId}/queue/alertes
        messagingTemplate.convertAndSend("/user/" + utilisateurId + "/queue/alertes", alerte);
    }
}
