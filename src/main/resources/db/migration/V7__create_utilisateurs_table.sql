-- Suppression si existe
DROP TABLE IF EXISTS utilisateurs CASCADE;

-- Création de la table utilisateurs
CREATE TABLE utilisateurs (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(254) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20),
    departement VARCHAR(100),
    poste VARCHAR(100),
    role VARCHAR(50) NOT NULL DEFAULT 'OBSERVATEUR',
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index
CREATE INDEX idx_utilisateurs_email ON utilisateurs(email);
CREATE INDEX idx_utilisateurs_role ON utilisateurs(role);

-- Données de test (mots de passe hashés en bcrypt — tous = 'password123')
INSERT INTO utilisateurs (email, password, nom, prenom, role, actif)
VALUES
    ('admin@ecarto.com', '$2a$08$LkWbPOMa6UQzFR00E3hixe89Z.RMqnf.JitEZb0dJYefOOAKPfLVG', 'Admin', 'System', 'ADMINISTRATEUR_SYSTEME', TRUE),
    ('sophie.martin@ecarto.com', '$2a$08$LkWbPOMa6UQzFR00E3hixe89Z.RMqnf.JitEZb0dJYefOOAKPfLVG', 'Martin', 'Sophie', 'CHEF_DE_PROJET', TRUE),
    ('marie.dubois@ecarto.com', '$2a$08$LkWbPOMa6UQzFR00E3hixe89Z.RMqnf.JitEZb0dJYefOOAKPfLVG', 'Dubois', 'Marie', 'OBSERVATEUR', TRUE);