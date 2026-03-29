-- =========================================================
-- V14: Workflow enrichi + Suivi temps réel des projets
-- Comités, Entités, Phases, Étapes, Validations, Alertes
-- =========================================================

-- 1. Table des entités (CIE, SODECI, GS2E, ERANOVE)
CREATE TABLE entites (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    couleur_theme VARCHAR(7),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO entites (code, nom, couleur_theme) VALUES
    ('CIE', 'Compagnie Ivoirienne d''Électricité', '#FF6F00'),
    ('SODECI', 'Société de Distribution d''Eau de Côte d''Ivoire', '#1565C0'),
    ('GS2E', 'Groupement des Services Eaux et Électricité', '#2E7D32'),
    ('ERANOVE', 'Groupe ERANOVE', '#6A1B9A');

-- 2. Table des comités de gouvernance
CREATE TABLE comites (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    nom VARCHAR(150) NOT NULL,
    description TEXT,
    president_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comite_president FOREIGN KEY (president_id) REFERENCES utilisateurs(id) ON DELETE SET NULL
);

INSERT INTO comites (code, nom, description) VALUES
    ('SI_CLIENTS', 'Comité SI Expérience Clients', 'Applications orientées clients et gestion de la relation client'),
    ('IOT_SMART_GRID', 'Comité SI Techniques, IoT et Smart Grid', 'Technologies opérationnelles et objets connectés'),
    ('SI_SALARIES', 'Comité SI Expérience Salariés', 'Solutions RH, Finance et Logistique'),
    ('TRANSFORMATION_DIGITALE', 'Comité Support Transformation Digitale', 'BI, Data, API Management et Digital Workplace'),
    ('SAPHIR', 'Comité SAPHIR', 'Évolution de la solution de gestion clientèle');

-- 3. Enrichir la table projets avec les nouvelles colonnes
-- Supprimer l'ancienne contrainte de check sur statut
ALTER TABLE projets DROP CONSTRAINT IF EXISTS projets_statut_check;

-- ⚠️ PostgreSQL interdit de modifier une colonne utilisée dans une vue.
-- On supprime la vue AVANT l'ALTER COLUMN, et on la recrée à la fin.
DROP VIEW IF EXISTS vue_statistiques_projets;

-- Élargir la colonne statut pour accepter les nouveaux statuts (IDEE, CADRAGE, etc.)
ALTER TABLE projets ALTER COLUMN statut TYPE VARCHAR(30);

-- Ajouter la nouvelle contrainte élargie (après l'ALTER TYPE)
ALTER TABLE projets ADD CONSTRAINT projets_statut_check
    CHECK (statut IN ('PREVU', 'EN_COURS', 'TERMINE', 'ANNULE',
                      'IDEE', 'CADRAGE', 'ETUDE_FAISABILITE', 'VALIDE',
                      'EN_PAUSE', 'RECETTE', 'DEPLOIEMENT', 'EN_PRODUCTION', 'CLOTURE', 'REJETE'));

-- Ajouter les nouvelles colonnes
ALTER TABLE projets ADD COLUMN IF NOT EXISTS comite_id BIGINT;
ALTER TABLE projets ADD COLUMN IF NOT EXISTS entite_id BIGINT;
ALTER TABLE projets ADD COLUMN IF NOT EXISTS budget_consomme DECIMAL(12, 2) DEFAULT 0;

ALTER TABLE projets ADD CONSTRAINT fk_projet_comite FOREIGN KEY (comite_id) REFERENCES comites(id) ON DELETE SET NULL;
ALTER TABLE projets ADD CONSTRAINT fk_projet_entite FOREIGN KEY (entite_id) REFERENCES entites(id) ON DELETE SET NULL;

CREATE INDEX idx_projets_comite ON projets(comite_id);
CREATE INDEX idx_projets_entite ON projets(entite_id);

-- 4. Table des phases du projet
CREATE TABLE phases (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    description TEXT,
    ordre INTEGER NOT NULL DEFAULT 1,
    projet_id BIGINT NOT NULL,
    date_debut_prevue DATE,
    date_fin_prevue DATE,
    date_debut_reelle DATE,
    date_fin_reelle DATE,
    progression INTEGER NOT NULL DEFAULT 0 CHECK (progression >= 0 AND progression <= 100),
    statut VARCHAR(30) NOT NULL DEFAULT 'A_VENIR' CHECK (statut IN ('A_VENIR', 'EN_COURS', 'TERMINEE', 'EN_RETARD', 'BLOQUEE')),
    verrouillee BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_phase_projet FOREIGN KEY (projet_id) REFERENCES projets(id) ON DELETE CASCADE
);

CREATE INDEX idx_phases_projet ON phases(projet_id);
CREATE INDEX idx_phases_statut ON phases(statut);

-- 5. Table des étapes (unité de travail granulaire)
CREATE TABLE etapes (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    description TEXT,
    ordre INTEGER NOT NULL DEFAULT 1,
    phase_id BIGINT NOT NULL,
    responsable_id BIGINT,
    date_echeance DATE NOT NULL,
    date_realisation DATE,
    duree_estimee_jours INTEGER,
    duree_reelle_jours INTEGER,
    statut VARCHAR(30) NOT NULL DEFAULT 'A_FAIRE'
        CHECK (statut IN ('A_FAIRE', 'EN_COURS', 'EN_ATTENTE_VALIDATION', 'VALIDEE', 'REJETEE', 'EN_RETARD', 'BLOQUEE')),
    validation_requise BOOLEAN NOT NULL DEFAULT true,
    bloquante BOOLEAN NOT NULL DEFAULT false,
    type_livrable VARCHAR(30) DEFAULT 'AUCUN'
        CHECK (type_livrable IN ('DOCUMENT', 'CODE_SOURCE', 'MAQUETTE', 'RAPPORT', 'PV_REUNION', 'LIVRABLE_TECHNIQUE', 'AUCUN')),
    url_livrable TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_etape_phase FOREIGN KEY (phase_id) REFERENCES phases(id) ON DELETE CASCADE,
    CONSTRAINT fk_etape_responsable FOREIGN KEY (responsable_id) REFERENCES utilisateurs(id) ON DELETE SET NULL
);

CREATE INDEX idx_etapes_phase ON etapes(phase_id);
CREATE INDEX idx_etapes_responsable ON etapes(responsable_id);
CREATE INDEX idx_etapes_statut ON etapes(statut);
CREATE INDEX idx_etapes_date_echeance ON etapes(date_echeance);

-- 6. Table des validations d'étapes
CREATE TABLE validations_etapes (
    id BIGSERIAL PRIMARY KEY,
    etape_id BIGINT NOT NULL,
    validateur_id BIGINT NOT NULL,
    decision VARCHAR(30) NOT NULL CHECK (decision IN ('APPROUVEE', 'REJETEE', 'DEMANDE_MODIFICATION')),
    commentaire TEXT,
    date_validation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    pieces_jointes TEXT,
    CONSTRAINT fk_validation_etape FOREIGN KEY (etape_id) REFERENCES etapes(id) ON DELETE CASCADE,
    CONSTRAINT fk_validation_validateur FOREIGN KEY (validateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

CREATE INDEX idx_validations_etape ON validations_etapes(etape_id);
CREATE INDEX idx_validations_validateur ON validations_etapes(validateur_id);

-- 7. Table des alertes (retards, notifications automatiques)
CREATE TABLE alertes (
    id BIGSERIAL PRIMARY KEY,
    projet_id BIGINT,
    etape_id BIGINT,
    type VARCHAR(30) NOT NULL CHECK (type IN (
        'RETARD_ETAPE', 'RETARD_PHASE', 'ETAPE_A_VALIDER', 'ETAPE_REJETEE',
        'BUDGET_DEPASSE', 'JALON_PROCHE', 'PHASE_TERMINEE', 'PROJET_BLOQUE')),
    niveau VARCHAR(20) NOT NULL CHECK (niveau IN ('INFORMATION', 'AVERTISSEMENT', 'IMPORTANT', 'URGENT', 'CRITIQUE')),
    message TEXT NOT NULL,
    lue BOOLEAN NOT NULL DEFAULT false,
    resolue BOOLEAN NOT NULL DEFAULT false,
    destinataire_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alerte_projet FOREIGN KEY (projet_id) REFERENCES projets(id) ON DELETE CASCADE,
    CONSTRAINT fk_alerte_etape FOREIGN KEY (etape_id) REFERENCES etapes(id) ON DELETE CASCADE,
    CONSTRAINT fk_alerte_destinataire FOREIGN KEY (destinataire_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

CREATE INDEX idx_alertes_projet ON alertes(projet_id);
CREATE INDEX idx_alertes_destinataire ON alertes(destinataire_id);
CREATE INDEX idx_alertes_lue ON alertes(lue);
CREATE INDEX idx_alertes_niveau ON alertes(niveau);

-- 8. Table historique des changements de statut
CREATE TABLE historique_statuts (
    id BIGSERIAL PRIMARY KEY,
    projet_id BIGINT NOT NULL,
    statut_avant VARCHAR(30),
    statut_apres VARCHAR(30) NOT NULL,
    utilisateur_id BIGINT,
    motif TEXT,
    date_changement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historique_projet FOREIGN KEY (projet_id) REFERENCES projets(id) ON DELETE CASCADE,
    CONSTRAINT fk_historique_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE SET NULL
);

CREATE INDEX idx_historique_projet ON historique_statuts(projet_id);
CREATE INDEX idx_historique_date ON historique_statuts(date_changement);

-- 9. Mettre à jour la vue de statistiques
DROP VIEW IF EXISTS vue_statistiques_projets;
CREATE VIEW vue_statistiques_projets AS
SELECT
    COUNT(*) as total_projets,
    COUNT(CASE WHEN statut = 'EN_COURS' THEN 1 END) as projets_en_cours,
    COUNT(CASE WHEN statut = 'TERMINE' OR statut = 'CLOTURE' THEN 1 END) as projets_termines,
    COUNT(CASE WHEN statut = 'PREVU' OR statut = 'IDEE' THEN 1 END) as projets_prevus,
    COUNT(CASE WHEN statut = 'ANNULE' OR statut = 'REJETE' THEN 1 END) as projets_annules,
    COUNT(CASE WHEN statut = 'RECETTE' THEN 1 END) as projets_recette,
    COUNT(CASE WHEN statut = 'EN_PRODUCTION' THEN 1 END) as projets_production,
    COALESCE(SUM(budget), 0) as budget_total,
    AVG(progression) as progression_moyenne
FROM projets;
