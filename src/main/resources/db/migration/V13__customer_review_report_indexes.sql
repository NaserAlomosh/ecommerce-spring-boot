CREATE INDEX idx_users_role_created ON users (role, created_at);
CREATE INDEX idx_reviews_product_created_rating ON reviews (product_id, created_at, rating);
CREATE INDEX idx_orders_status_completed_customer ON orders (status, completed_at, customer_id);
