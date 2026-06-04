-- Assigner une entité par défaut aux projets qui n'en auraient pas (afin de ne pas faire échouer la contrainte NOT NULL)
DO $$ 
DECLARE
    default_entite_id BIGINT;
BEGIN
    SELECT id INTO default_entite_id FROM entites ORDER BY id ASC LIMIT 1;
    
    IF default_entite_id IS NOT NULL THEN
        UPDATE projets SET entite_id = default_entite_id WHERE entite_id IS NULL;
    END IF;
END $$;

-- Rendre la colonne entite_id NOT NULL
ALTER TABLE projets ALTER COLUMN entite_id SET NOT NULL;

-- Ajouter la colonne pour le rapport principal
ALTER TABLE projets 
    ADD COLUMN rapport_principal_id BIGINT;

-- Ajouter la contrainte de clé étrangère
ALTER TABLE projets
    ADD CONSTRAINT fk_projet_rapport_principal 
    FOREIGN KEY (rapport_principal_id) 
    REFERENCES rapports(id) ON DELETE SET NULL;
