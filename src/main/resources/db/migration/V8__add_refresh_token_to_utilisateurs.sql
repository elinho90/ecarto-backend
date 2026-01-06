-- Ajout des colonnes pour la gestion du refresh token
ALTER TABLE utilisateurs 
ADD COLUMN IF NOT EXISTS refresh_token VARCHAR(500),
ADD COLUMN IF NOT EXISTS refresh_token_expiry TIMESTAMP;