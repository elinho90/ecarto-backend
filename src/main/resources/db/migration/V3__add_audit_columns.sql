-- Ajout des colonnes d'audit manquantes
ALTER TABLE projets 
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
    ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);  -- ← IMPORTANT : updated_by, pas last_modified_by

-- Si vous utilisez aussi les dates d'audit (décommentez si nécessaire)
-- ALTER TABLE projets 
--     ADD COLUMN IF NOT EXISTS created_date TIMESTAMP,
--     ADD COLUMN IF NOT EXISTS last_modified_date TIMESTAMP;