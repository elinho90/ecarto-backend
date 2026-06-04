-- Ajout de la colonne pour le versioning métier des rapports (documents)
ALTER TABLE rapports 
ADD COLUMN IF NOT EXISTS document_version INTEGER DEFAULT 1 NOT NULL;
