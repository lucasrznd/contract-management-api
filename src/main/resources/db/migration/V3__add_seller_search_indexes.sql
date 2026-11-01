-- Trigram indexes for the fields used by SellerService text search.
-- The expression must match exactly what TextSearchSpecification generates: immutable_unaccent(lower(column)).
CREATE INDEX idx_seller_name_search ON tb_seller USING gin (immutable_unaccent(lower(name)) gin_trgm_ops);
CREATE INDEX idx_seller_phone_number_search ON tb_seller USING gin (immutable_unaccent(lower(phone_number)) gin_trgm_ops);
