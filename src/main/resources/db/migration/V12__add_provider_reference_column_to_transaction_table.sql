ALTER TABLE transactions
ADD COLUMN provider_reference_id VARCHAR(255);

CREATE INDEX idx_transactions_provider_reference_id
ON transactions(provider_reference_id);