-- 1. Create the new junction table
CREATE TABLE IF NOT EXISTS merchant_customers (
    merchant_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    PRIMARY KEY (merchant_id, customer_id),
    FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);


-- 2. Drop the old foreign key and column from customers
ALTER TABLE customers DROP FOREIGN KEY fk_customer_merchant;
ALTER TABLE customers DROP COLUMN merchant_id;

-- 3. Adjust the unique constraint
-- Old: CONSTRAINT uq_customer UNIQUE (merchant_id, email)
-- New: email is globally unique across all customers
ALTER TABLE customers ADD CONSTRAINT uq_customer_email UNIQUE (email);
