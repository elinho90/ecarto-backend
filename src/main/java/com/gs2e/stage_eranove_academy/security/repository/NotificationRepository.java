package com.gs2e.stage_eranove_academy.security.repository;

import com.gs2e.stage_eranove_academy.security.model.Notification;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUtilisateurOrderByCreatedAtDesc(Utilisateur utilisateur, Pageable pageable);

    List<Notification> findTop10ByUtilisateurOrderByCreatedAtDesc(Utilisateur utilisateur);

    long countByUtilisateurAndLuFalse(Utilisateur utilisateur);

    @Modifying
    @Query("UPDATE Notification n SET n.lu = true WHERE n.utilisateur = :utilisateur AND n.lu = false")
    void markAllAsRead(@Param("utilisateur") Utilisateur utilisateur);

    @Modifying
    @Query("UPDATE Notification n SET n.lu = true WHERE n.id = :id AND n.utilisateur = :utilisateur")
    void markAsRead(@Param("id") Long id, @Param("utilisateur") Utilisateur utilisateur);
}
