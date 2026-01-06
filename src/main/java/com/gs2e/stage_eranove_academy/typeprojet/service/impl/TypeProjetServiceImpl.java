package com.gs2e.stage_eranove_academy.typeprojet.service.impl;

import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.common.Exceptions.ErrorCodes;
import com.gs2e.stage_eranove_academy.common.Exceptions.InvalidEntityException;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import com.gs2e.stage_eranove_academy.security.service.AuthService;
import com.gs2e.stage_eranove_academy.security.service.NotificationService;
import com.gs2e.stage_eranove_academy.typeprojet.dto.TypeProjetDto;
import com.gs2e.stage_eranove_academy.typeprojet.mapper.TypeProjetMapper;
import com.gs2e.stage_eranove_academy.typeprojet.repository.TypeProjetRepository;
import com.gs2e.stage_eranove_academy.typeprojet.service.TypeProjetService;
import com.gs2e.stage_eranove_academy.typeprojet.model.TypeProjet;
import com.gs2e.stage_eranove_academy.typeprojet.validator.TypeProjetDtoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TypeProjetServiceImpl implements TypeProjetService {

    private final TypeProjetRepository typeProjetRepository;
    private final TypeProjetMapper typeProjetMapper;
    private final NotificationService notificationService;
    private final AuthService authService;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedProjectTypes() {
        log.info("Vérification des types de projet par défaut...");

        java.util.Map<String, String> defaultTypes = new java.util.LinkedHashMap<>();
        defaultTypes.put("Infrastructures Électriques",
                "Extension, maintenance ou modernisation du réseau électrique.");
        defaultTypes.put("Adduction d'Eau Potable", "Réseaux de distribution d'eau, forages et stations.");
        defaultTypes.put("Télécoms & Fibre Optique", "Infrastructures numériques, fibre optique et sites mobiles.");
        defaultTypes.put("Génie Civil & Bâtiment", "Construction et rénovation de bâtiments techniques.");
        defaultTypes.put("Énergie Renouvelable (Solaire)", "Parcs solaires et solutions d'énergie hybrides.");

        int createdCount = 0;
        int reactivatedCount = 0;
        for (java.util.Map.Entry<String, String> entry : defaultTypes.entrySet()) {
            java.util.Optional<TypeProjet> existing = typeProjetRepository.findByNomNative(entry.getKey());
            if (existing.isPresent()) {
                TypeProjet type = existing.get();
                if (type.getEstActif() == null || !type.getEstActif()) {
                    type.setEstActif(true);
                    typeProjetRepository.save(type);
                    reactivatedCount++;
                }
            } else {
                TypeProjet type = new TypeProjet();
                type.setNom(entry.getKey());
                type.setDescription(entry.getValue());
                type.setEstActif(true);
                typeProjetRepository.save(type);
                createdCount++;
            }
        }

        if (createdCount > 0 || reactivatedCount > 0) {
            log.info("{} types de projet créés, {} réactivés.", createdCount, reactivatedCount);
        } else {
            log.info("Tous les types de projet par défaut sont déjà actifs.");
        }
    }

    @Override
    public TypeProjetDto create(TypeProjetDto typeProjetDto) {

        List<String> errors = TypeProjetDtoValidator.validate(typeProjetDto);

        if (!errors.isEmpty()) {
            log.error("le typeprojet n'est pas valide {}", typeProjetDto);
            throw new InvalidEntityException("le typeprojet n'est pas valide ", ErrorCodes.TYPE_PROJET_NOT_VALID,
                    errors);
        }

        boolean isUpdate = typeProjetDto.getId() != null;
        TypeProjetDto savedType = typeProjetMapper.toDto(
                typeProjetRepository.save(typeProjetMapper.toEntity(typeProjetDto)));

        // Notification
        try {
            Utilisateur currentUser = authService.getCurrentUser();
            notificationService.createNotification(
                    currentUser,
                    isUpdate ? "TYPE_PROJET_UPDATE" : "TYPE_PROJET_CREATION",
                    isUpdate ? "Type de projet mis à jour" : "Nouveau type de projet créé",
                    "Le type de projet '" + savedType.getNom() + "' a été " + (isUpdate ? "mis à jour." : "créé."));
        } catch (Exception e) {
            log.warn("Impossible de créer la notification pour le type de projet: {}", e.getMessage());
        }

        return savedType;
    }

    @Override
    public Page<TypeProjetDto> getAll(Pageable pageable) {
        return typeProjetRepository.findAll(pageable).map(typeProjetMapper::toDto);
    }

    @Override
    public TypeProjetDto getById(Long id) {
        if (id == null) {
            log.error("typeProjet ID est null");
            return null;
        }
        return typeProjetRepository.findById(id)
                .map(typeProjetMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun typeProjet avec l'ID = " + id + " n'a été trouvé dans la BDD",
                        ErrorCodes.TYPE_PROJET_NOT_FOUND));
    }

    @Override
    public void delete(Long id) {
        TypeProjetDto typeProjetDto = getById(id);
        typeProjetRepository.deleteById(typeProjetDto.getId());

        // Notification
        try {
            Utilisateur currentUser = authService.getCurrentUser();
            notificationService.createNotification(
                    currentUser,
                    "TYPE_PROJET_DELETION",
                    "Type de projet supprimé",
                    "Le type de projet '" + typeProjetDto.getNom() + "' a été supprimé.");
        } catch (Exception e) {
            log.warn("Impossible de créer la notification de suppression de type de projet: {}", e.getMessage());
        }
    }
}
