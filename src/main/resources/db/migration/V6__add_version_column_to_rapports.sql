-- V6__add_version_column_to_rapports.sql
ALTER TABLE rapports
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;