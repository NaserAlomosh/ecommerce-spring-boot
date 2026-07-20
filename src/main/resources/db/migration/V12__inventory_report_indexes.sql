CREATE INDEX idx_inventory_history_product_movement_at ON inventory_history(product_id, movement_at);
CREATE INDEX idx_inventory_history_movement_type_movement_at ON inventory_history(movement_type, movement_at);
CREATE INDEX idx_inventory_history_performed_by ON inventory_history(performed_by_id);
CREATE INDEX idx_products_active_stock ON products(active, stock_quantity);
