CREATE TABLE webhook_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id VARCHAR(64) NOT NULL UNIQUE,
    transaction_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    url TEXT NOT NULL,
    payload JSON NOT NULL,
    status VARCHAR(32) NOT NULL, -- PENDING, DELIVERED, PERMANENTLY_FAILED
    attempts INT NOT NULL DEFAULT 0,
    next_attempt_at DATETIME(3) NULL,
    lats_error TEXT,
    created_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
);

CREATE INDEX idx_webhook_next_attempt_at ON webhook_events(next_attempt_at);
CREATE INDEX idx_webhook_status ON webhook_events(status);