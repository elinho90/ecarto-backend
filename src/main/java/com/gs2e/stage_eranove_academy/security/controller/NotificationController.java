package com.gs2e.stage_eranove_academy.security.controller;

import com.gs2e.stage_eranove_academy.security.dto.NotificationDto;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import com.gs2e.stage_eranove_academy.security.service.AuthService;
import com.gs2e.stage_eranove_academy.security.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "API de gestion des notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    @GetMapping
    @Operation(summary = "Récupérer toutes les notifications de l'utilisateur")
    public ResponseEntity<Page<NotificationDto>> getNotifications(
            @PageableDefault(size = 20) Pageable pageable) {
        Utilisateur currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(notificationService.getNotifications(currentUser, pageable));
    }

    @GetMapping("/recent")
    @Operation(summary = "Récupérer les 10 notifications les plus récentes")
    public ResponseEntity<List<NotificationDto>> getRecentNotifications() {
        Utilisateur currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(notificationService.getRecentNotifications(currentUser));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Récupérer le nombre de notifications non lues")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        Utilisateur currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(Map.of("unreadCount", notificationService.getUnreadCount(currentUser)));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marquer une notification comme lue")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        Utilisateur currentUser = authService.getCurrentUser();
        notificationService.markAsRead(id, currentUser);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Marquer toutes les notifications comme lues")
    public ResponseEntity<Void> markAllAsRead() {
        Utilisateur currentUser = authService.getCurrentUser();
        notificationService.markAllAsRead(currentUser);
        return ResponseEntity.ok().build();
    }
}
