CREATE TABLE guest_order_links (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_guest_order_links_slug UNIQUE (slug)
);

ALTER TABLE orders ADD COLUMN guest_order BOOLEAN NOT NULL DEFAULT FALSE AFTER customer_id;
ALTER TABLE orders ADD COLUMN guest_order_link_id BIGINT NULL AFTER guest_order;
ALTER TABLE orders ADD CONSTRAINT fk_orders_guest_order_link
    FOREIGN KEY (guest_order_link_id) REFERENCES guest_order_links(id);
CREATE INDEX idx_orders_guest_order_link ON orders(guest_order_link_id);
CREATE INDEX idx_guest_order_links_active ON guest_order_links(active, created_at);
