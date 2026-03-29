-- =============================================================================
-- V11 : Correction des colonnes manquantes
-- Alignement entre les entités JPA et la structure réelle de la base
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. TABLE type_projet : colonnes présentes dans l'entité Java mais absentes en DB
-- -----------------------------------------------------------------------------
ALTER TABLE type_projet
    ADD COLUMN IF NOT EXISTS libelle   VARCHAR(100),
    ADD COLUMN IF NOT EXISTS est_actif BOOLEAN NOT NULL DEFAULT TRUE;

-- Colonnes d'audit héritées de AuditModel (manquantes dans le script V1)
ALTER TABLE type_projet
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
    ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

-- Mettre à jour les lignes existantes (heure courante comme valeur par défaut)
UPDATE type_projet
SET created_at = NOW(), updated_at = NOW()
WHERE created_at IS NULL;

-- Forcer NOT NULL maintenant que les données sont remplie
ALTER TABLE type_projet
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL;


-- -----------------------------------------------------------------------------
-- 2. TABLE utilisateurs : colonne dashboard_config (définie dans Utilisateur.java)
-- -----------------------------------------------------------------------------
ALTER TABLE utilisateurs
    ADD COLUMN IF NOT EXISTS dashboard_config TEXT;


-- -----------------------------------------------------------------------------
-- 3. TABLE projets : homogénéisation TIMESTAMPTZ → TIMESTAMP
--    (V4 avait utilisé TIMESTAMPTZ, incohérent avec le reste du schéma)
-- -----------------------------------------------------------------------------
ALTER TABLE projets
    ALTER COLUMN created_at TYPE TIMESTAMP USING created_at::TIMESTAMP,
    ALTER COLUMN updated_at TYPE TIMESTAMP USING updated_at::TIMESTAMP;


-- -----------------------------------------------------------------------------
-- 4. TABLE rapports : homogénéisation TIMESTAMPTZ → TIMESTAMP
--    (V5 avait utilisé TIMESTAMPTZ)
-- -----------------------------------------------------------------------------
ALTER TABLE rapports
    ALTER COLUMN created_at TYPE TIMESTAMP USING created_at::TIMESTAMP,
    ALTER COLUMN updated_at TYPE TIMESTAMP USING updated_at::TIMESTAMP;
