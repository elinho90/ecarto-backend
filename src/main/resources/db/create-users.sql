-- Script SQL pour créer les utilisateurs de test pour E-Carto
-- Mot de passe pour tous: "string" (hashé avec BCrypt)
-- Hash BCrypt pour "string": $2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4667O/VNmYYV8xYMzHvFXdmNXSxK

-- Suppression des utilisateurs existants (optionnel, décommenter si nécessaire)
-- DELETE FROM utilisateurs WHERE email IN ('admin.systeme@ecarto.com', 'chef.projet@ecarto.com', 'analyste@ecarto.com', 'developpeur@ecarto.com', 'decideur@ecarto.com', 'observateur@ecarto.com');

-- 1. Administrateur Système
INSERT INTO utilisateurs (email, password, nom, prenom, role, created_at, updated_at)
SELECT 'admin.systeme@ecarto.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4667O/VNmYYV8xYMzHvFXdmNXSxK', 'Système', 'Admin', 'ADMINISTRATEUR_SYSTEME', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'admin.systeme@ecarto.com');

-- 2. Chef de Projet
INSERT INTO utilisateurs (email, password, nom, prenom, role, created_at, updated_at)
SELECT 'chef.projet@ecarto.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4667O/VNmYYV8xYMzHvFXdmNXSxK', 'Projet', 'Chef', 'CHEF_DE_PROJET', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'chef.projet@ecarto.com');

-- 3. Analyste / Chargé d'étude
INSERT INTO utilisateurs (email, password, nom, prenom, role, created_at, updated_at)
SELECT 'analyste@ecarto.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4667O/VNmYYV8xYMzHvFXdmNXSxK', 'Kouassi', 'Marie', 'ANALYSTE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'analyste@ecarto.com');

-- 4. Développeur / Équipe technique
INSERT INTO utilisateurs (email, password, nom, prenom, role, created_at, updated_at)
SELECT 'developpeur@ecarto.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4667O/VNmYYV8xYMzHvFXdmNXSxK', 'Koné', 'Ibrahim', 'DEVELOPPEUR', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'developpeur@ecarto.com');

-- 5. Décideur / Direction
INSERT INTO utilisateurs (email, password, nom, prenom, role, created_at, updated_at)
SELECT 'decideur@ecarto.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4667O/VNmYYV8xYMzHvFXdmNXSxK', 'Diallo', 'Aminata', 'DECIDEUR', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'decideur@ecarto.com');

-- 6. Observateur
INSERT INTO utilisateurs (email, password, nom, prenom, role, created_at, updated_at)
SELECT 'observateur@ecarto.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4667O/VNmYYV8xYMzHvFXdmNXSxK', 'Traoré', 'Yao', 'OBSERVATEUR', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'observateur@ecarto.com');

-- Vérification des utilisateurs créés
SELECT id, email, nom, prenom, role FROM utilisateurs ORDER BY role;
