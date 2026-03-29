-- =============================================================================
-- V12 : Formalisation des tables sites et tasks
-- Ces tables étaient créées dynamiquement par JPA (ddl-auto=update) sans
-- contraintes SQL explicites ni index. Cette migration les formalise.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. TABLE sites : index sur colonnes clés de filtrage
-- -----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_sites_ville    ON sites(ville);
CREATE INDEX IF NOT EXISTS idx_sites_region   ON sites(region);
CREATE INDEX IF NOT EXISTS idx_sites_statut   ON sites(statut);
CREATE INDEX IF NOT EXISTS idx_sites_type     ON sites(type);

-- Contrainte CHECK sur l'enum TypeSite
ALTER TABLE sites DROP CONSTRAINT IF EXISTS chk_sites_type;
ALTER TABLE sites ADD CONSTRAINT chk_sites_type
    CHECK (type IN ('SIEGE_SOCIAL','BUREAU_REGIONAL','CENTRE_OPERATIONNEL','DATACENTER','SITE_CLIENT','FORMATION'));

-- Contrainte CHECK sur l'enum StatutSite
ALTER TABLE sites DROP CONSTRAINT IF EXISTS chk_sites_statut;
ALTER TABLE sites ADD CONSTRAINT chk_sites_statut
    CHECK (statut IN ('ACTIF','INACTIF','EN_CONSTRUCTION','EN_MAINTENANCE'));


-- -----------------------------------------------------------------------------
-- 2. TABLE tasks : index sur colonnes clés de filtrage
-- -----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_tasks_projet_id ON tasks(projet_id);
CREATE INDEX IF NOT EXISTS idx_tasks_statut    ON tasks(statut);

-- Contrainte CHECK sur l'enum StatutTask
ALTER TABLE tasks DROP CONSTRAINT IF EXISTS chk_tasks_statut;
ALTER TABLE tasks ADD CONSTRAINT chk_tasks_statut
    CHECK (statut IN ('TODO','IN_PROGRESS','DONE'));

-- Contrainte de clé étrangère explicite (si pas déjà créée par JPA)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_tasks_projet' AND table_name = 'tasks'
    ) THEN
        ALTER TABLE tasks
            ADD CONSTRAINT fk_tasks_projet
            FOREIGN KEY (projet_id) REFERENCES projets(id) ON DELETE CASCADE;
    END IF;
END $$;


-- -----------------------------------------------------------------------------
-- 3. TABLE projets : contraintes CHECK sur les enums (manquantes dans V1 PATCH)
-- -----------------------------------------------------------------------------
-- Re-définir avec un nom cohérent pour la documentation
ALTER TABLE projets DROP CONSTRAINT IF EXISTS chk_projets_statut;
ALTER TABLE projets ADD CONSTRAINT chk_projets_statut
    CHECK (statut IN ('PREVU','EN_COURS','TERMINE','ANNULE'));

ALTER TABLE projets DROP CONSTRAINT IF EXISTS chk_projets_priorite;
ALTER TABLE projets ADD CONSTRAINT chk_projets_priorite
    CHECK (priorite IN ('FAIBLE','MOYENNE','HAUTE','CRITIQUE'));


-- -----------------------------------------------------------------------------
-- 4. TABLE rapports : contrainte CHECK sur l'enum NiveauRisque
-- -----------------------------------------------------------------------------
ALTER TABLE rapports DROP CONSTRAINT IF EXISTS chk_rapports_risque;
ALTER TABLE rapports ADD CONSTRAINT chk_rapports_risque
    CHECK (risque IN ('FAIBLE','MOYEN','ELEVE','CRITIQUE'));
