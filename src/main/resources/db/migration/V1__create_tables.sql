-- Création des tables principales pour E-Carto

-- Table: type_projet
CREATE TABLE type_projet (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    couleur VARCHAR(7),
    icone VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: projets
CREATE TABLE projets (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    statut VARCHAR(20) NOT NULL CHECK (statut IN ('PREVU', 'EN_COURS', 'TERMINE', 'ANNULE')),
    priorite VARCHAR(20) NOT NULL CHECK (priorite IN ('FAIBLE', 'MOYENNE', 'HAUTE', 'CRITIQUE')),
    responsable VARCHAR(100) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin_prevue DATE,
    date_fin_reelle DATE,
    budget DECIMAL(12, 2),
    progression INTEGER NOT NULL DEFAULT 0 CHECK (progression >= 0 AND progression <= 100),
    type_projet_id BIGINT,
    tags VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_type_projet FOREIGN KEY (type_projet_id) REFERENCES type_projet(id) ON DELETE SET NULL
);

-- Table: projet_equipe (table d'association pour les membres de l'équipe)
CREATE TABLE projet_equipe (
    projet_id BIGINT NOT NULL,
    membre VARCHAR(100) NOT NULL,
    PRIMARY KEY (projet_id, membre),
    CONSTRAINT fk_projet_equipe_projet FOREIGN KEY (projet_id) REFERENCES projets(id) ON DELETE CASCADE
);

-- Table: utilisateurs
CREATE TABLE utilisateurs (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'UTILISATEUR' CHECK (role IN ('ADMIN', 'CHEF_PROJET', 'UTILISATEUR')),
    telephone VARCHAR(20),
    departement VARCHAR(100),
    poste VARCHAR(100),
    actif BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: rapports
CREATE TABLE rapports (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    description TEXT,
    fichier_nom VARCHAR(255) NOT NULL,
    fichier_type VARCHAR(10) NOT NULL CHECK (fichier_type IN ('pdf', 'doc', 'docx')),
    fichier_taille BIGINT NOT NULL,
    fichier_chemin TEXT NOT NULL,
    projet_id BIGINT,
    uploade_par VARCHAR(100) NOT NULL,
    faisabilite INTEGER NOT NULL CHECK (faisabilite >= 0 AND faisabilite <= 100),
    risque VARCHAR(20) NOT NULL CHECK (risque IN ('FAIBLE', 'MOYEN', 'ELEVE', 'CRITIQUE')),
    budget_estime DECIMAL(12, 2),
    duree_estimee_mois INTEGER NOT NULL CHECK (duree_estimee_mois > 0),
    recommandations TEXT,
    analyse_automatique BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rapport_projet FOREIGN KEY (projet_id) REFERENCES projets(id) ON DELETE SET NULL
);

-- Table: notifications
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    titre VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    lu BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

-- Table: parametres_systeme
CREATE TABLE parametres_systeme (
    id BIGSERIAL PRIMARY KEY,
    cle VARCHAR(100) NOT NULL UNIQUE,
    valeur TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index pour améliorer les performances
CREATE INDEX idx_projets_statut ON projets(statut);
CREATE INDEX idx_projets_responsable ON projets(responsable);
CREATE INDEX idx_projets_date_debut ON projets(date_debut);
CREATE INDEX idx_projets_type_projet ON projets(type_projet_id);
CREATE INDEX idx_projets_nom ON projets(nom);

CREATE INDEX idx_rapports_projet ON rapports(projet_id);
CREATE INDEX idx_rapports_fichier_type ON rapports(fichier_type);
CREATE INDEX idx_rapports_uploade_par ON rapports(uploade_par);
CREATE INDEX idx_rapports_risque ON rapports(risque);
CREATE INDEX idx_rapports_faisabilite ON rapports(faisabilite);

CREATE INDEX idx_utilisateurs_email ON utilisateurs(email);
CREATE INDEX idx_utilisateurs_actif ON utilisateurs(actif);

CREATE INDEX idx_notifications_utilisateur ON notifications(utilisateur_id);
CREATE INDEX idx_notifications_lu ON notifications(lu);

-- Insertion des données initiales
INSERT INTO type_projet (nom, description, couleur, icone) VALUES
    ('Développement Web', 'Projets de développement d''applications web', '#3B82F6', 'fas fa-globe'),
    ('Développement Mobile', 'Projets de développement d''applications mobiles', '#10B981', 'fas fa-mobile-alt'),
    ('Infrastructure', 'Projets d''infrastructure et de déploiement', '#F59E0B', 'fas fa-server'),
    ('Sécurité', 'Projets de sécurité et de conformité', '#EF4444', 'fas fa-shield-alt'),
    ('Data & Analytics', 'Projets de données et d''analyse', '#8B5CF6', 'fas fa-chart-bar'),
    ('Intégration', 'Projets d''intégration de systèmes', '#06B6D4', 'fas fa-plug');

INSERT INTO utilisateurs (email, mot_de_passe, nom, prenom, role) VALUES
    ('admin@ecarto.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iOEcalwu', 'Admin', 'System', 'ADMIN'),
    ('sophie.martin@ecarto.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iOEcalwu', 'Martin', 'Sophie', 'CHEF_PROJET'),
    ('jean.dupont@ecarto.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iOEcalwu', 'Dupont', 'Jean', 'CHEF_PROJET'),
    ('marie.dubois@ecarto.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iOEcalwu', 'Dubois', 'Marie', 'UTILISATEUR'),
    ('pierre.bernard@ecarto.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iOEcalwu', 'Bernard', 'Pierre', 'UTILISATEUR'),
    ('claire.laurent@ecarto.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iOEcalwu', 'Laurent', 'Claire', 'UTILISATEUR'),
    ('thomas.robert@ecarto.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iOEcalwu', 'Robert', 'Thomas', 'UTILISATEUR');

INSERT INTO parametres_systeme (cle, valeur, description) VALUES
    ('app.name', 'E-Carto', 'Nom de l''application'),
    ('app.version', '1.0.0', 'Version de l''application'),
    ('app.upload.max_size', '10485760', 'Taille maximale des fichiers uploadés (10MB)'),
    ('app.upload.allowed_types', 'pdf,doc,docx', 'Types de fichiers autorisés'),
    ('security.jwt.expiration', '86400000', 'Durée de validité du token JWT (24h)'),
    ('security.jwt.refresh_expiration', '604800000', 'Durée de validité du refresh token (7j)'),
    ('notifications.enabled', 'true', 'Activation des notifications'),
    ('email.enabled', 'true', 'Activation de l''envoi d''emails'),
    ('analytics.enabled', 'true', 'Activation de l''analyse des rapports');

-- Création des vues pour les statistiques
CREATE VIEW vue_statistiques_projets AS
SELECT 
    COUNT(*) as total_projets,
    COUNT(CASE WHEN statut = 'EN_COURS' THEN 1 END) as projets_en_cours,
    COUNT(CASE WHEN statut = 'TERMINE' THEN 1 END) as projets_termines,
    COUNT(CASE WHEN statut = 'PREVU' THEN 1 END) as projets_prevus,
    COUNT(CASE WHEN statut = 'ANNULE' THEN 1 END) as projets_annules,
    COALESCE(SUM(budget), 0) as budget_total,
    AVG(progression) as progression_moyenne
FROM projets;

CREATE VIEW vue_statistiques_rapports AS
SELECT 
    COUNT(*) as total_rapports,
    COUNT(CASE WHEN fichier_type = 'pdf' THEN 1 END) as rapports_pdf,
    COUNT(CASE WHEN fichier_type = 'docx' THEN 1 END) as rapports_word,
    AVG(faisabilite) as faisabilite_moyenne,
    COUNT(CASE WHEN risque = 'FAIBLE' THEN 1 END) as risques_faibles,
    COUNT(CASE WHEN risque = 'MOYEN' THEN 1 END) as risques_moyens,
    COUNT(CASE WHEN risque = 'ELEVE' THEN 1 END) as risques_eleves,
    COUNT(CASE WHEN risque = 'CRITIQUE' THEN 1 END) as risques_critiques,
    COALESCE(SUM(budget_estime), 0) as budget_estime_total
FROM rapports;