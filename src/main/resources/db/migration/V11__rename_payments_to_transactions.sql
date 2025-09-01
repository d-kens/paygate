-- Rename the table
ALTER TABLE payments RENAME TO transactions;

-- Update constraints names if necessary
ALTER TABLE transactions
    DROP CONSTRAINT fk_payment_merchant,
    DROP CONSTRAINT fk_payment_customer,
    DROP CONSTRAINT uq_reference;

-- Recreate constraints with new table name
ALTER TABLE transactions
    ADD CONSTRAINT fk_transaction_merchant FOREIGN KEY (merchant_id) REFERENCES merchants(id);

ALTER TABLE transactions
    ADD CONSTRAINT fk_transaction_customer FOREIGN KEY (customer_id) REFERENCES customers(id);

ALTER TABLE transactions
    ADD CONSTRAINT uq_transaction_reference UNIQUE (provider, payment_reference);
