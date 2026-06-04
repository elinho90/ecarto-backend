-- =========================================================
-- V19: Correction des hashes BCrypt des comptes complémentaires
--      Remplacement du préfixe $2b$ (bcryptjs Node.js) par $2a$ (Spring Security)
-- =========================================================
-- Les comptes existent déjà en base avec des hashes invalides ($2b$).
-- On met à jour uniquement le champ password.
-- =========================================================

-- Analyste — mot de passe: Analyste2026!
UPDATE utilisateurs
SET password = '$2a$10$Sy2tCRNatFAOzmKjhmSGye/WkO9UwzNUsxRds9chfDEwRZn8h6Qz.',
    updated_at = NOW()
WHERE email = 'analyste@ecarto.com'
  AND password LIKE '$2b$%';

-- Développeur — mot de passe: Developpeur2026!
UPDATE utilisateurs
SET password = '$2a$10$KPHA1Hvjseowbd2Onvz5gOKeTWitTVy6WfYqbn3BenPP79F9CJlZC',
    updated_at = NOW()
WHERE email = 'developpeur@ecarto.com'
  AND password LIKE '$2b$%';

-- Décideur — mot de passe: Decideur2026!
UPDATE utilisateurs
SET password = '$2a$10$42X/M.d7.dh730Kv5Eq3p.gDs8pIGQJCbPlHPd9zKct/OzNuUmmPC',
    updated_at = NOW()
WHERE email = 'decideur@ecarto.com'
  AND password LIKE '$2b$%';
