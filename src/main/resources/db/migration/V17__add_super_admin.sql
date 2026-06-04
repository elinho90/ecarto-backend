-- Création d'un utilisateur Super Admin dédié
-- Email: superadmin@ecarto.com
-- Mot de passe en clair: SuperAdmin2026!
-- Hash généré de manière sécurisée (BCrypt)

INSERT INTO utilisateurs (email, password, nom, prenom, role, actif)
VALUES ('superadmin@ecarto.com', '$2a$10$su/4W5z5GbUWIsvRNNc2Du3aM/7DoDgSyuHniPfjQlay70KJ1OXkS', 'Super', 'Admin', 'ADMINISTRATEUR_SYSTEME', TRUE)
ON CONFLICT (email) DO UPDATE 
SET password = EXCLUDED.password, 
    role = EXCLUDED.role;
