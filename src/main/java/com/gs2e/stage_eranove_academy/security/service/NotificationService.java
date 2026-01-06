package com.gs2e.stage_eranove_academy.security.service;

import com.gs2e.stage_eranove_academy.security.dto.NotificationDto;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    Page<NotificationDto> getNotifications(Utilisateur utilisateur, Pageable pageable);

    List<NotificationDto> getRecentNotifications(Utilisateur utilisateur);

    long getUnreadCount(Utilisateur utilisateur);

    void markAsRead(Long id, Utilisateur utilisateur);

    void markAllAsRead(Utilisateur utilisateur);

    void createNotification(Utilisateur utilisateur, String type, String titre, String message);
}
