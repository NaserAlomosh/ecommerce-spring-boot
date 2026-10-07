CREATE TABLE inventory_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    movement_type VARCHAR(50) NOT NULL,
    quantity_before INT NOT NULL,
    quantity_change INT NOT NULL,
    quantity_after INT NOT NULL,
    order_id BIGINT NULL,
    order_number VARCHAR(30),
    customer_id BIGINT NULL,
    customer_name_snapshot VARCHAR(180),
    performed_by_id BIGINT NULL,
    note VARCHAR(500),
    movement_at TIMESTAMP(6) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_inventory_history_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_inventory_history_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_inventory_history_performed_by FOREIGN KEY (performed_by_id) REFERENCES users(id),
    CONSTRAINT chk_inventory_history_quantity_after CHECK (quantity_after >= 0),
    CONSTRAINT chk_inventory_history_quantity_math CHECK (quantity_after = quantity_before + quantity_change)
);
CREATE INDEX idx_inventory_history_product ON inventory_history(product_id);
CREATE INDEX idx_inventory_history_movement_type ON inventory_history(movement_type);
CREATE INDEX idx_inventory_history_created_at ON inventory_history(created_at);
CREATE INDEX idx_inventory_history_order ON inventory_history(order_id);
CREATE INDEX idx_inventory_history_customer ON inventory_history(customer_id);
CREATE INDEX idx_inventory_history_order_number ON inventory_history(order_number);
