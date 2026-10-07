DROP INDEX uk_inventory_history_order_product_movement ON inventory_history;

CREATE INDEX idx_inventory_history_order_product_movement
    ON inventory_history(order_id, product_id, movement_type);
