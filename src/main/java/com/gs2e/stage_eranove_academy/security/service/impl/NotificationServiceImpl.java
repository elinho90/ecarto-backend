package com.gs2e.stage_eranove_academy.security.service.impl;

import com.gs2e.stage_eranove_academy.security.dto.NotificationDto;
import com.gs2e.stage_eranove_academy.security.model.Notification;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import com.gs2e.stage_eranove_academy.security.repository.NotificationRepository;
import com.gs2e.stage_eranove_academy.security.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotifications(Utilisateur utilisateur, Pageable pageable) {
        return notificationRepository.findByUtilisateurOrderByCreatedAtDesc(utilisateur, pageable)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getRecentNotifications(Utilisateur utilisateur) {
        return notificationRepository.findTop10ByUtilisateurOrderByCreatedAtDesc(utilisateur)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Utilisateur utilisateur) {
        return notificationRepository.countByUtilisateurAndLuFalse(utilisateur);
    }

    @Override
    public void markAsRead(Long id, Utilisateur utilisateur) {
        notificationRepository.markAsRead(id, utilisateur);
    }

    @Override
    public void markAllAsRead(Utilisateur utilisateur) {
        notificationRepository.markAllAsRead(utilisateur);
    }

    @Override
    public void createNotification(Utilisateur utilisateur, String type, String titre, String message) {
        Notification notification = Notification.builder()
                .utilisateur(utilisateur)
                .type(type)
                .titre(titre)
                .message(message)
                .lu(false)
                .build();
        notificationRepository.save(notification);
        log.info("🔔 Notification créée pour {}: {}", utilisateur.getEmail(), titre);
    }

    private NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .utilisateurId(notification.getUtilisateur().getId())
                .type(notification.getType())
                .titre(notification.getTitre())
                .message(notification.getMessage())
                .lu(notification.getLu())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
