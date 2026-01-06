-- Script d'initialisation de la base de données
-- Ce script est exécuté au premier démarrage du conteneur PostgreSQL

-- Création de la base de données si elle n'existe pas
SELECT 'CREATE DATABASE e_carto_db WITH OWNER e_carto_user'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'e_carto_db')\gexec

-- Activation des extensions nécessaires
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Configuration des paramètres PostgreSQL
ALTER SYSTEM SET max_connections = 200;
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
ALTER SYSTEM SET maintenance_work_mem = '64MB';
ALTER SYSTEM SET checkpoint_completion_target = 0.9;
ALTER SYSTEM SET wal_buffers = '16MB';
ALTER SYSTEM SET default_statistics_target = 100;
ALTER SYSTEM SET random_page_cost = 1.1;
ALTER SYSTEM SET effective_io_concurrency = 200;

-- Configuration pour les logs
ALTER SYSTEM SET log_statement = 'all';
ALTER SYSTEM SET log_duration = on;
ALTER SYSTEM SET log_line_prefix = '%t [%p]: [%l-1] user=%u,db=%d,app=%a,client=%h ';
ALTER SYSTEM SET log_checkpoints = on;
ALTER SYSTEM SET log_connections = on;
ALTER SYSTEM SET log_disconnections = on;
ALTER SYSTEM SET log_lock_waits = on;
ALTER SYSTEM SET log_temp_files = 0;

-- Sélection de la base de données
\c e_carto_db;

-- Configuration des schémas
CREATE SCHEMA IF NOT EXISTS audit;
CREATE SCHEMA IF NOT EXISTS reporting;

-- Configuration des droits
GRANT ALL PRIVILEGES ON SCHEMA public TO e_carto_user;
GRANT ALL PRIVILEGES ON SCHEMA audit TO e_carto_user;
GRANT ALL PRIVILEGES ON SCHEMA reporting TO e_carto_user;

-- Configuration par défaut pour les timestamps
SET timezone = 'UTC';

-- Message de confirmation
SELECT 'Base de données E-Carto initialisée avec succès' AS status;