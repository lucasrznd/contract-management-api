-- Extensions used by the server-side text search (TextSearchSpecification).
-- pg_trgm: trigram GIN indexes, so LIKE '%term%' can use an index instead of a sequential scan.
-- unaccent: accent-insensitive search ("joao" matches "João").
-- Both are "trusted" extensions (PostgreSQL 13+), so the database owner can create them without superuser.
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS unaccent;

-- unaccent() is only STABLE, and PostgreSQL requires IMMUTABLE functions in index expressions.
-- This wrapper pins the dictionary explicitly, which makes it safe to declare as IMMUTABLE.
-- Search predicates and indexes must both use: immutable_unaccent(lower(column)).
CREATE OR REPLACE FUNCTION immutable_unaccent(text)
    RETURNS text
    LANGUAGE sql
    IMMUTABLE
    PARALLEL SAFE
    STRICT
AS
$$
SELECT public.unaccent('public.unaccent'::regdictionary, $1)
$$;
