ALTER TABLE contact_messages
    ADD COLUMN is_read BOOLEAN NOT NULL DEFAULT FALSE AFTER message,
    ADD COLUMN read_at TIMESTAMP(6) NULL AFTER is_read,
    ADD INDEX idx_contact_messages_read_created (is_read, created_at);
