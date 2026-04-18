-- remove authority from accounts
ALTER TABLE accounts DROP COLUMN IF EXISTS authority;