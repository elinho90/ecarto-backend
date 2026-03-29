package com.gs2e.stage_eranove_academy.historique.service;

import com.gs2e.stage_eranove_academy.historique.dto.HistoriqueStatutDto;
import com.gs2e.stage_eranove_academy.historique.model.HistoriqueStatut;
import com.gs2e.stage_eranove_academy.historique.repository.HistoriqueStatutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoriqueStatutService {

    private final HistoriqueStatutRepository historiqueStatutRepository;

    @Transactional(readOnly = true)
    public List<HistoriqueStatutDto> getHistoriqueByProjet(Long projetId) {
        return historiqueStatutRepository.findByProjetIdOrderByDateChangementDesc(projetId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private HistoriqueStatutDto toDto(HistoriqueStatut historique) {
        HistoriqueStatutDto dto = new HistoriqueStatutDto();
        dto.setId(historique.getId());
        
        if (historique.getProjet() != null) {
            dto.setProjetId(historique.getProjet().getId());
            dto.setProjetNom(historique.getProjet().getNom());
        }
        
        dto.setStatutAvant(historique.getStatutAvant());
        dto.setStatutApres(historique.getStatutApres());
        
        if (historique.getUtilisateur() != null) {
            dto.setUtilisateurId(historique.getUtilisateur().getId());
            dto.setUtilisateurNom(historique.getUtilisateur().getNom() + " " + historique.getUtilisateur().getPrenom());
        }
        
        dto.setMotif(historique.getMotif());
        dto.setDateChangement(historique.getDateChangement());
        
        return dto;
    }
}
