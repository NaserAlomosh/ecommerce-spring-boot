CREATE INDEX idx_orders_status_completed_at ON orders(status, completed_at);
CREATE INDEX idx_orders_delivery_user_created ON orders(assigned_delivery_user_id, created_at);
CREATE INDEX idx_order_status_history_status_changed ON order_status_history(new_status, changed_at);
CREATE INDEX idx_order_items_product_order ON order_items(product_id, order_id);
CREATE INDEX idx_products_category ON products(category_id);
