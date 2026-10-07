CREATE TABLE customer_addresses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    recipient_name VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    city VARCHAR(100) NOT NULL,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    area VARCHAR(100),
    street VARCHAR(255),
    additional_directions VARCHAR(500),
    default_address BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_customer_addresses_customer FOREIGN KEY (customer_id) REFERENCES users(id),
    CONSTRAINT chk_customer_addresses_latitude CHECK (latitude >= -90 AND latitude <= 90),
    CONSTRAINT chk_customer_addresses_longitude CHECK (longitude >= -180 AND longitude <= 180)
);

CREATE INDEX idx_customer_addresses_customer ON customer_addresses(customer_id);
CREATE INDEX idx_customer_addresses_customer_active ON customer_addresses(customer_id, active);
CREATE INDEX idx_customer_addresses_customer_default ON customer_addresses(customer_id, default_address);
