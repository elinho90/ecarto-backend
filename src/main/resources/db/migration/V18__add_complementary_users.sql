-- =========================================================
-- V18: Insertion des comptes complémentaires de test
--      avec mots de passe sécurisés (BCrypt $2a$ — compatible Spring Security)
-- =========================================================
-- Analyste     : analyste@ecarto.com      / Analyste2026!
-- Développeur  : developpeur@ecarto.com   / Developpeur2026!
-- Décideur     : decideur@ecarto.com      / Decideur2026!
-- =========================================================
-- Note: hash générés avec BCryptPasswordEncoder Java (force 10)
--       Préfixe $2a$ requis pour la compatibilité avec Spring Security
-- =========================================================

-- 1. Analyste / Chargée d'étude — mot de passe: Analyste2026!
INSERT INTO utilisateurs (email, password, nom, prenom, role, actif, created_at, updated_at)
SELECT
    'analyste@ecarto.com',
    '$2a$10$Sy2tCRNatFAOzmKjhmSGye/WkO9UwzNUsxRds9chfDEwRZn8h6Qz.',
    'Kouassi',
    'Marie',
    'ANALYSTE',
    TRUE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM utilisateurs WHERE email = 'analyste@ecarto.com'
);

-- 2. Développeur / Équipe technique — mot de passe: Developpeur2026!
INSERT INTO utilisateurs (email, password, nom, prenom, role, actif, created_at, updated_at)
SELECT
    'developpeur@ecarto.com',
    '$2a$10$KPHA1Hvjseowbd2Onvz5gOKeTWitTVy6WfYqbn3BenPP79F9CJlZC',
    'Koné',
    'Ibrahim',
    'DEVELOPPEUR',
    TRUE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM utilisateurs WHERE email = 'developpeur@ecarto.com'
);

-- 3. Décideur / Direction — mot de passe: Decideur2026!
INSERT INTO utilisateurs (email, password, nom, prenom, role, actif, created_at, updated_at)
SELECT
    'decideur@ecarto.com',
    '$2a$10$42X/M.d7.dh730Kv5Eq3p.gDs8pIGQJCbPlHPd9zKct/OzNuUmmPC',
    'Diallo',
    'Aminata',
    'DECIDEUR',
    TRUE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM utilisateurs WHERE email = 'decideur@ecarto.com'
);

-- Vérification
SELECT id, email, nom, prenom, role, actif FROM utilisateurs ORDER BY role;
