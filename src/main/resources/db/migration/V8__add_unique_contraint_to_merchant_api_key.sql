ALTER TABLE merchants
ADD CONSTRAINT unique_merchant_api_key UNIQUE (api_key)