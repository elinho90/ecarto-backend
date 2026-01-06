-- Script de réparation pour les références orphelines de TypeProjet
-- Ce script peut être exécuté pour corriger les données en base

-- 1. Vérifier les TypeProjet manquants
SELECT DISTINCT p.type_projet_id 
FROM projets p 
LEFT JOIN type_projet tp ON p.type_projet_id = tp.id 
WHERE p.type_projet_id IS NOT NULL AND tp.id IS NULL;

-- 2. Option A: Insérer les TypeProjet manquants (si supprimés accidentellement)
INSERT INTO type_projet (id, nom, description, couleur, icone, created_at, updated_at) VALUES
    (1, 'Développement Web', 'Projets de développement d''applications web', '#3B82F6', 'fas fa-globe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Développement Mobile', 'Projets de développement d''applications mobiles', '#10B981', 'fas fa-mobile-alt', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'Infrastructure', 'Projets d''infrastructure et de déploiement', '#F59E0B', 'fas fa-server', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'Sécurité', 'Projets de sécurité et de conformité', '#EF4444', 'fas fa-shield-alt', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'Data & Analytics', 'Projets de données et d''analyse', '#8B5CF6', 'fas fa-chart-bar', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 'Intégration', 'Projets d''intégration de systèmes', '#06B6D4', 'fas fa-plug', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 3. Ajuster la séquence si nécessaire
SELECT setval('type_projet_id_seq', (SELECT MAX(id) FROM type_projet));

-- 4. Option B: Mettre NULL les références orphelines (si vous ne voulez pas réinsérer les types)
-- ATTENTION: Décommentez uniquement si vous voulez retirer les références
-- UPDATE projets SET type_projet_id = NULL WHERE type_projet_id NOT IN (SELECT id FROM type_projet);

-- 5. Vérification finale
SELECT 
    p.id,
    p.nom,
    p.type_projet_id,
    tp.nom as type_projet_nom
FROM projets p
LEFT JOIN type_projet tp ON p.type_projet_id = tp.id
WHERE p.type_projet_id IS NOT NULL
ORDER BY p.id;
