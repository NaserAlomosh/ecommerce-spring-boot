CREATE TABLE employee_sales_links (
    id BIGINT NOT NULL AUTO_INCREMENT,
    token_hash CHAR(64) NOT NULL,
    employee_id BIGINT NOT NULL,
    expires_at TIMESTAMP(6) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_employee_sales_links_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_employee_sales_links_employee FOREIGN KEY (employee_id) REFERENCES users(id)
);
CREATE INDEX idx_employee_sales_links_employee ON employee_sales_links(employee_id, created_at);

ALTER TABLE orders MODIFY customer_id BIGINT NULL;
ALTER TABLE orders ADD COLUMN sales_link_id BIGINT NULL AFTER customer_id;
ALTER TABLE orders ADD COLUMN referred_by_user_id BIGINT NULL AFTER sales_link_id;
ALTER TABLE orders ADD CONSTRAINT fk_orders_sales_link FOREIGN KEY (sales_link_id) REFERENCES employee_sales_links(id);
ALTER TABLE orders ADD CONSTRAINT fk_orders_referred_by FOREIGN KEY (referred_by_user_id) REFERENCES users(id);
CREATE INDEX idx_orders_sales_link ON orders(sales_link_id);
CREATE INDEX idx_orders_referred_by ON orders(referred_by_user_id, created_at);
ALTER TABLE website_settings
    ADD COLUMN social_sales_link VARCHAR(1000) NULL AFTER linkedin_url;
