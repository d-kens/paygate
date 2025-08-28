ALTER TABLE merchants
 ADD CONSTRAINT uq_merchants_user_id UNIQUE (user_id);
