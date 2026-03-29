package com.gs2e.stage_eranove_academy.risque.service;

import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.projet.repository.ProjetRepository;
import com.gs2e.stage_eranove_academy.risque.dto.RisqueDto;
import com.gs2e.stage_eranove_academy.risque.mapper.RisqueMapper;
import com.gs2e.stage_eranove_academy.risque.model.Risque;
import com.gs2e.stage_eranove_academy.risque.model.StatutRisque;
import com.gs2e.stage_eranove_academy.risque.repository.RisqueRepository;
import com.gs2e.stage_eranove_academy.security.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RisqueService {

    private final RisqueRepository risqueRepository;
    private final ProjetRepository projetRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final RisqueMapper risqueMapper;

    public RisqueService(RisqueRepository risqueRepository, ProjetRepository projetRepository,
                         UtilisateurRepository utilisateurRepository, RisqueMapper risqueMapper) {
        this.risqueRepository = risqueRepository;
        this.projetRepository = projetRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.risqueMapper = risqueMapper;
    }

    public List<RisqueDto> findAllByProjet(Long projetId) {
        return risqueMapper.toDtoList(risqueRepository.findByProjetId(projetId));
    }

    public RisqueDto createRisque(RisqueDto dto) {
        Risque risque = new Risque();
        risque.setProjet(projetRepository.findById(dto.getProjetId())
                .orElseThrow(() -> new EntityNotFoundException("Projet introuvable")));
        
        risque.setTitre(dto.getTitre());
        risque.setDescription(dto.getDescription());
        risque.setProbabilite(dto.getProbabilite());
        risque.setImpact(dto.getImpact());
        risque.setPlanMitigation(dto.getPlanMitigation());

        if (dto.getResponsableId() != null) {
            risque.setResponsable(utilisateurRepository.findById(dto.getResponsableId())
                    .orElseThrow(() -> new EntityNotFoundException("Responsable introuvable")));
        }

        return risqueMapper.toDto(risqueRepository.save(risque));
    }

    public RisqueDto updateStatut(Long risqueId, String statut) {
        Risque risque = risqueRepository.findById(risqueId)
                .orElseThrow(() -> new EntityNotFoundException("Risque introuvable avec l'id: " + risqueId));

        try {
            risque.setStatut(StatutRisque.valueOf(statut.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Statut de risque invalide : '" + statut + 
                "'. Valeurs acceptées : IDENTIFIE, EN_COURS_MITIGATION, CLOS, ACCEPTE");
        }

        if ("CLOS".equalsIgnoreCase(statut)) {
            risque.setDateResolution(java.time.LocalDate.now());
        }

        return risqueMapper.toDto(risqueRepository.save(risque));
    }
}
