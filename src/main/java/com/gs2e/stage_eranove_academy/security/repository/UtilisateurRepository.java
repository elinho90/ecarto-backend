package com.gs2e.stage_eranove_academy.security.repository;

import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM Utilisateur u WHERE u.refreshToken = :refreshToken AND u.refreshTokenExpiry > :now")
    Optional<Utilisateur> findByRefreshTokenAndExpiryAfter(@Param("refreshToken") String refreshToken, @Param("now") LocalDateTime now);

    @Query("SELECT u FROM Utilisateur u WHERE u.refreshToken = :refreshToken")
    Optional<Utilisateur> findByRefreshToken(@Param("refreshToken") String refreshToken);

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.actif = true")
    long countActiveUsers();

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role")
    long countByRole(@Param("role") Utilisateur.Role role);
}