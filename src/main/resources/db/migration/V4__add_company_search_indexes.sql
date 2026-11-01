-- Trigram indexes for the fields used by CompanyService text search.
-- The expression must match exactly what TextSearchSpecification generates: immutable_unaccent(lower(column)).
CREATE INDEX idx_company_business_name_search ON tb_company USING gin (immutable_unaccent(lower(business_name)) gin_trgm_ops);
CREATE INDEX idx_company_trade_name_search ON tb_company USING gin (immutable_unaccent(lower(trade_name)) gin_trgm_ops);
CREATE INDEX idx_company_registration_number_search ON tb_company USING gin (immutable_unaccent(lower(registration_number)) gin_trgm_ops);
CREATE INDEX idx_company_cpf_search ON tb_company USING gin (immutable_unaccent(lower(cpf)) gin_trgm_ops);
CREATE INDEX idx_company_city_search ON tb_company USING gin (immutable_unaccent(lower(city)) gin_trgm_ops);
