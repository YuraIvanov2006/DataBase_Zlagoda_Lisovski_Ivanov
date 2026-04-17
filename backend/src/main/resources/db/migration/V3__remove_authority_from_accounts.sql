-- V3__remove_authority_from_accounts.sql
ALTER TABLE accounts DROP COLUMN IF EXISTS authority;