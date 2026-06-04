-- Ajout de la colonne rapport_principal_id dans la table d'audit Envers (projets_aud)
ALTER TABLE projets_aud ADD COLUMN rapport_principal_id BIGINT;
