package com.gs2e.stage_eranove_academy.security.service;

import com.gs2e.stage_eranove_academy.security.dto.LoginRequest;
import com.gs2e.stage_eranove_academy.security.dto.LoginResponse;
import com.gs2e.stage_eranove_academy.security.dto.RegisterRequest;
import com.gs2e.stage_eranove_academy.security.model.Utilisateur;
import com.gs2e.stage_eranove_academy.security.repository.UtilisateurRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    // 🔐 MOT DE PASSE MAÎTRE - UNIQUEMENT POUR LES ADMINISTRATEURS
    @Value("${app.security.master-password:}")
    private String masterPassword;

    @Transactional
    public Utilisateur register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (utilisateurRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
        }

        Utilisateur newUser = new Utilisateur();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setNom(request.getNom());
        newUser.setPrenom(request.getPrenom());
        newUser.setTelephone(request.getTelephone());
        newUser.setDepartement(request.getDepartement());
        newUser.setPoste(request.getPoste());
        newUser.setRole(Utilisateur.Role.OBSERVATEUR);
        newUser.setActif(true);

        Utilisateur savedUser = utilisateurRepository.save(newUser);
        log.info("✅ Utilisateur inscrit avec succès : {}", email);
        return savedUser;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail().trim().toLowerCase();
        String password = loginRequest.getPassword();

        log.info("🔍 Tentative de connexion pour: {}", email);

        // Récupération de l'utilisateur
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("❌ Utilisateur non trouvé: {}", email);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
                });

        log.debug("👤 Utilisateur trouvé - ID: {}, Rôle: {}, Actif: {}", 
            user.getId(), user.getRole(), user.getActif());

        // Vérification du compte actif
        if (!user.getActif()) {
            log.warn("⛔ Compte désactivé: {}", email);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Compte désactivé");
        }

        // 🔐 VÉRIFICATION DU MOT DE PASSE
        boolean passwordMatches = verifyPassword(user, password);

        if (!passwordMatches) {
            log.warn("❌ Mot de passe incorrect pour: {}", email);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }

        // Génération du token
        String token = generateToken(user);
        log.info("✅ Connexion réussie pour: {} (Rôle: {})", email, user.getRole());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(token);
        response.setRefreshToken(generateRefreshToken(user));
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtExpiration / 1000); // en secondes
        response.setIssuedAt(LocalDateTime.now());
        response.setExpiresAt(LocalDateTime.now().plusSeconds(jwtExpiration / 1000));
        response.setUser(new LoginResponse.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getNom(),
                user.getPrenom(),
                user.getRole().name(),
                user.getDepartement(),
                user.getPoste()));

        return response;
    }

    /**
     * 🔐 LOGIQUE DE VÉRIFICATION DU MOT DE PASSE
     * - Les ADMINISTRATEURS peuvent utiliser le mot de passe maître OU leur propre mot de passe
     * - Les AUTRES RÔLES doivent utiliser uniquement leur propre mot de passe
     */
    private boolean verifyPassword(Utilisateur user, String password) {
        // 1️⃣ Vérification du mot de passe maître (UNIQUEMENT pour les administrateurs)
        if (user.getRole() == Utilisateur.Role.ADMINISTRATEUR_SYSTEME) {
            boolean isMasterPassword = masterPassword != null 
                && !masterPassword.isEmpty() 
                && masterPassword.equals(password);
            
            if (isMasterPassword) {
                log.info("🔓 Connexion ADMINISTRATEUR avec mot de passe maître pour: {}", user.getEmail());
                return true;
            }
        }

        // 2️⃣ Vérification du mot de passe standard (pour tous les utilisateurs)
        try {
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            log.debug("🔍 Vérification mot de passe standard pour {}: {}", 
                user.getEmail(), matches ? "✅ OK" : "❌ ÉCHEC");
            return matches;
        } catch (Exception e) {
            log.error("❌ ERREUR lors de la vérification du mot de passe pour {}: {}", 
                user.getEmail(), e.getMessage());
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erreur serveur lors de la vérification du mot de passe"
            );
        }
    }

    @Transactional(readOnly = true)
    public Utilisateur getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        String email = authentication.getName();

        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
    }

    private String generateToken(Utilisateur user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("nom", user.getNom())
                .claim("prenom", user.getPrenom())
                .claim("role", user.getRole().name())
                .issuedAt(new Date())
                .expiration(Date.from(
                    LocalDateTime.now()
                        .plusHours(24)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                ))
                .signWith(key)
                .compact();
    }

    private String generateRefreshToken(Utilisateur user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(Date.from(
                    LocalDateTime.now()
                        .plusDays(7)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                ))
                .signWith(key)
                .compact();
    }
}