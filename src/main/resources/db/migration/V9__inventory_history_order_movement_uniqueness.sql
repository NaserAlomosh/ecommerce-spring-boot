CREATE INDEX idx_inventory_history_order_product ON inventory_history(order_id, product_id);
CREATE UNIQUE INDEX uk_inventory_history_order_product_movement ON inventory_history(order_id, product_id, movement_type);
