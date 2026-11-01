-- Trigram index for the contract's own field used by ContractService text search.
-- The other searched fields (company trade/business name, seller name) are covered by V3 and V4 indexes.
CREATE INDEX idx_contract_advertising_order_search ON tb_contract USING gin (immutable_unaccent(lower(advertising_order)) gin_trgm_ops);

-- Foreign keys used by the joins of the search (PostgreSQL does not index foreign keys automatically).
CREATE INDEX idx_contract_client_company_id ON tb_contract (client_company_id);
CREATE INDEX idx_contract_seller_id ON tb_contract (seller_id);
