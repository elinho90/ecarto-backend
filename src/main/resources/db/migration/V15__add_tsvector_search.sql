CREATE EXTENSION IF NOT EXISTS unaccent;

-- En PostgreSQL, la fonction unaccent() est "STABLE" et non "IMMUTABLE".
-- Or, une colonne GENERATED ALWAYS doit utiliser uniquement des fonctions IMMUTABLE.
-- On crée donc une fonction wrapper IMMUTABLE.
CREATE OR REPLACE FUNCTION f_unaccent(text)
  RETURNS text AS
$func$
SELECT unaccent($1)
$func$  LANGUAGE sql IMMUTABLE;

-- Ajout de la colonne tsvector pour la recherche plein texte
ALTER TABLE projets 
ADD COLUMN IF NOT EXISTS document_vectors tsvector 
GENERATED ALWAYS AS (
    to_tsvector('french', f_unaccent(coalesce(nom, '')) || ' ' || f_unaccent(coalesce(description, '')) || ' ' || f_unaccent(coalesce(tags, '')))
) STORED;

-- Création de l'index GIN pour accélérer la recherche Full-Text
CREATE INDEX IF NOT EXISTS idx_projet_search ON projets USING GIN(document_vectors);
