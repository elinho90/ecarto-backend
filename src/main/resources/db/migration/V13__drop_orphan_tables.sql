-- =============================================================================
-- V13 : Suppression des tables orphelines
-- Version sécurisée : utilise un bloc PL/pgSQL pour gérer les erreurs
-- de permissions sans bloquer la migration.
-- =============================================================================

-- Suppression de la table de relation en premier (contrainte FK)
DO $$
BEGIN
    -- Tenter de supprimer utilisateur_permissions
    EXECUTE 'DROP TABLE IF EXISTS utilisateur_permissions CASCADE';
    RAISE NOTICE 'Table utilisateur_permissions supprimée avec succès.';
EXCEPTION
    WHEN insufficient_privilege THEN
        RAISE WARNING 'Impossible de supprimer utilisateur_permissions : privilèges insuffisants. Suppression manuelle requise.';
    WHEN OTHERS THEN
        RAISE WARNING 'Erreur lors de la suppression de utilisateur_permissions : %', SQLERRM;
END $$;

-- Suppression de la table permissions
DO $$
BEGIN
    EXECUTE 'DROP TABLE IF EXISTS permissions CASCADE';
    RAISE NOTICE 'Table permissions supprimée avec succès.';
EXCEPTION
    WHEN insufficient_privilege THEN
        RAISE WARNING 'Impossible de supprimer permissions : privilèges insuffisants. Suppression manuelle requise.';
    WHEN OTHERS THEN
        RAISE WARNING 'Erreur lors de la suppression de permissions : %', SQLERRM;
END $$;
