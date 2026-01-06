-- Suppression si existe
DROP TABLE IF EXISTS utilisateurs CASCADE;

-- Création de la table utilisateurs
CREATE TABLE utilisateurs (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20),
    departement VARCHAR(100),
    poste VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'UTILISATEUR',
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index
CREATE INDEX idx_utilisateurs_email ON utilisateurs(email);
CREATE INDEX idx_utilisateurs_role ON utilisateurs(role);

-- Données de test
INSERT INTO utilisateurs (email, password, nom, prenom, role, actif) 
VALUES 
    ('admin@ecarto.com', 'admin123', 'Admin', 'System', 'ADMIN', TRUE),
    ('sophie.martin@ecarto.com', 'password123', 'Martin', 'Sophie', 'CHEF_PROJET', TRUE),
    ('marie.dubois@ecarto.com', 'password123', 'Dubois', 'Marie', 'UTILISATEUR', TRUE);