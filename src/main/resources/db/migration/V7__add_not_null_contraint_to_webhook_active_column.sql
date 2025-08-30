-- Replace null values with TRUE (or FALSE depending on your business logic)
UPDATE merchants
SET webhook_active = TRUE
WHERE webhook_active IS NULL;

-- Then enforce NOT NULL + default
ALTER TABLE merchants
    MODIFY COLUMN webhook_active BOOLEAN NOT NULL DEFAULT TRUE;
