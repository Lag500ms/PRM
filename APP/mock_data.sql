-- =============================================
-- MOCK DATA FOR PRM PROJECT (PostgreSQL)
-- =============================================
-- Đọc kĩ BE entities để tạo đúng schema:
-- - OrderStatus: PENDING, CONFIRMED, COMPLETED, CANCELLED
-- - ScheduleStatus: PENDING, CONFIRMED, COMPLETED, CANCELLED
-- - CustomerInfo: @Embeddable (customer, phone, address)
-- - Password: BCrypt hash của "password123"
-- =============================================

-- 1. CATEGORIES
INSERT INTO categories (id, name) VALUES
('cat-001', 'SUV'),
('cat-002', 'Sedan'),
('cat-003', 'Sport'),
('cat-004', 'Electric');

-- 2. ACCOUNTS
-- AccountDetails: @Embeddable (full_name, phone, address)
-- Password "password123" → BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- Note: admin123 và dealer36 đã được tạo bởi DataInitializer, chỉ cần lấy ID

-- Lấy ID của admin (đã tồn tại)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM accounts WHERE username = 'admin123') THEN
        RAISE EXCEPTION 'Admin account not found. Please start BE first to run DataInitializer.';
    END IF;
END $$;

-- Update dealer36 account để có ID cố định (nếu chưa có)
UPDATE accounts 
SET full_name = 'Dealer 36', 
    phone = '0912345678', 
    address = 'District 1, HCM'
WHERE username = 'dealer36';

-- Thêm dealers khác
INSERT INTO accounts (id, username, password, email, role, is_active, created_at, updated_at, full_name, phone, address) VALUES
('acc-dealer-002', 'dealer_hcm', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'dealer_hcm@example.com', 'DEALER', true, NOW(), NOW(), 'Dealer HCM', '0923456789', 'District 3, HCM'),
('acc-dealer-003', 'dealer_hanoi', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'dealer_hanoi@example.com', 'DEALER', true, NOW(), NOW(), 'Dealer Hanoi', '0934567890', 'Hoan Kiem, Hanoi')
ON CONFLICT (username) DO NOTHING;

-- 3. VEHICLES (tạo bởi admin, dùng ID từ accounts)
INSERT INTO vehicles (id, account_id, category_id, name, color, price, model, version, image, quantity, created_at, updated_at) 
SELECT 'veh-001', a.id, 'cat-001', 'Tesla Model X', 'White', 95000.00, '2024', 'Long Range', 'model-x.jpg', 50, NOW(), NOW()
FROM accounts a WHERE a.username = 'admin123'
ON CONFLICT (id) DO NOTHING;

INSERT INTO vehicles (id, account_id, category_id, name, color, price, model, version, image, quantity, created_at, updated_at) 
SELECT 'veh-002', a.id, 'cat-002', 'Tesla Model 3', 'Black', 45000.00, '2024', 'Standard', 'model-3.jpg', 100, NOW(), NOW()
FROM accounts a WHERE a.username = 'admin123'
ON CONFLICT (id) DO NOTHING;

INSERT INTO vehicles (id, account_id, category_id, name, color, price, model, version, image, quantity, created_at, updated_at) 
SELECT 'veh-003', a.id, 'cat-003', 'Tesla Roadster', 'Red', 200000.00, '2024', 'Founders Series', 'roadster.jpg', 10, NOW(), NOW()
FROM accounts a WHERE a.username = 'admin123'
ON CONFLICT (id) DO NOTHING;

INSERT INTO vehicles (id, account_id, category_id, name, color, price, model, version, image, quantity, created_at, updated_at) 
SELECT 'veh-004', a.id, 'cat-004', 'Tesla Model S', 'Blue', 90000.00, '2024', 'Plaid', 'model-s.jpg', 30, NOW(), NOW()
FROM accounts a WHERE a.username = 'admin123'
ON CONFLICT (id) DO NOTHING;

INSERT INTO vehicles (id, account_id, category_id, name, color, price, model, version, image, quantity, created_at, updated_at) 
SELECT 'veh-005', a.id, 'cat-001', 'Tesla Model Y', 'Gray', 55000.00, '2024', 'Performance', 'model-y.jpg', 75, NOW(), NOW()
FROM accounts a WHERE a.username = 'admin123'
ON CONFLICT (id) DO NOTHING;

-- 4. INVENTORIES (mỗi dealer có 1 inventory, dùng ID từ accounts)
INSERT INTO inventories (id, account_id)
SELECT 'inv-001', a.id FROM accounts a WHERE a.username = 'dealer36'
ON CONFLICT (id) DO NOTHING;

INSERT INTO inventories (id, account_id) VALUES
('inv-002', 'acc-dealer-002'),
('inv-003', 'acc-dealer-003')
ON CONFLICT (id) DO NOTHING;

-- 5. VEHICLE_INVENTORY (xe trong kho của từng dealer)
INSERT INTO vehicle_inventory (id, inventory_id, vehicle_id, quantity) VALUES
-- Dealer 1 (dealer36) inventory
('vi-001', 'inv-001', 'veh-001', 5),
('vi-002', 'inv-001', 'veh-002', 10),
('vi-003', 'inv-001', 'veh-005', 8),
-- Dealer 2 inventory
('vi-004', 'inv-002', 'veh-002', 15),
('vi-005', 'inv-002', 'veh-004', 3),
('vi-006', 'inv-002', 'veh-005', 12),
-- Dealer 3 inventory
('vi-007', 'inv-003', 'veh-001', 7),
('vi-008', 'inv-003', 'veh-003', 2),
('vi-009', 'inv-003', 'veh-004', 5)
ON CONFLICT (id) DO NOTHING;

-- 6. ORDERS (CustomerInfo: customer, phone, address)
-- Status: PENDING, CONFIRMED, COMPLETED, CANCELLED
-- Orders cho dealer36 (dùng ID từ accounts)
INSERT INTO orders (id, customer, phone, address, total_price, account_id, status, created_at, updated_at)
SELECT 'ord-001', 'Nguyen Van A', '0901111111', '123 Le Loi, District 1, HCM', 95000.00, a.id, 'PENDING', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'
FROM accounts a WHERE a.username = 'dealer36'
ON CONFLICT (id) DO NOTHING;

INSERT INTO orders (id, customer, phone, address, total_price, account_id, status, created_at, updated_at)
SELECT 'ord-002', 'Tran Thi B', '0902222222', '456 Tran Hung Dao, District 5, HCM', 45000.00, a.id, 'CONFIRMED', NOW() - INTERVAL '3 days', NOW() - INTERVAL '2 days'
FROM accounts a WHERE a.username = 'dealer36'
ON CONFLICT (id) DO NOTHING;

INSERT INTO orders (id, customer, phone, address, total_price, account_id, status, created_at, updated_at)
SELECT 'ord-003', 'Le Van C', '0903333333', '789 Nguyen Hue, District 1, HCM', 90000.00, a.id, 'COMPLETED', NOW() - INTERVAL '7 days', NOW() - INTERVAL '6 days'
FROM accounts a WHERE a.username = 'dealer36'
ON CONFLICT (id) DO NOTHING;

INSERT INTO orders (id, customer, phone, address, total_price, account_id, status, created_at, updated_at)
SELECT 'ord-004', 'Pham Thi D', '0904444444', '321 Hai Ba Trung, District 3, HCM', 55000.00, a.id, 'CANCELLED', NOW() - INTERVAL '10 days', NOW() - INTERVAL '9 days'
FROM accounts a WHERE a.username = 'dealer36'
ON CONFLICT (id) DO NOTHING;

-- Orders cho dealer_hcm
INSERT INTO orders (id, customer, phone, address, total_price, account_id, status, created_at, updated_at) VALUES
('ord-005', 'Hoang Van E', '0905555555', '654 Dong Khoi, District 1, HCM', 200000.00, 'acc-dealer-002', 'CONFIRMED', NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 day'),
('ord-006', 'Nguyen Thi F', '0906666666', '111 Ly Thuong Kiet, Hoan Kiem, Hanoi', 45000.00, 'acc-dealer-002', 'PENDING', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

-- Orders cho dealer_hanoi
INSERT INTO orders (id, customer, phone, address, total_price, account_id, status, created_at, updated_at) VALUES
('ord-007', 'Tran Van G', '0907777777', '222 Ba Trieu, Hoan Kiem, Hanoi', 95000.00, 'acc-dealer-003', 'PENDING', NOW(), NOW()),
('ord-008', 'Le Thi H', '0908888888', '333 Tran Phu, Hoan Kiem, Hanoi', 55000.00, 'acc-dealer-003', 'COMPLETED', NOW() - INTERVAL '8 days', NOW() - INTERVAL '7 days')
ON CONFLICT (id) DO NOTHING;

-- 7. SCHEDULES (CustomerInfo: customer, phone, address, dateTime)
-- Status: PENDING, CONFIRMED, COMPLETED, CANCELLED
-- Schedules cho dealer36 (dùng ID từ accounts)
INSERT INTO schedules (id, customer, phone, address, date_time, status, account_id, created_at, updated_at)
SELECT 'sch-001', 'Nguyen Van I', '0909999999', '100 Nguyen Trai, District 1, HCM', NOW() + INTERVAL '2 days', 'PENDING', a.id, NOW(), NOW()
FROM accounts a WHERE a.username = 'dealer36'
ON CONFLICT (id) DO NOTHING;

INSERT INTO schedules (id, customer, phone, address, date_time, status, account_id, created_at, updated_at)
SELECT 'sch-002', 'Le Thi J', '0900000000', '200 Le Duan, District 1, HCM', NOW() + INTERVAL '3 days', 'CONFIRMED', a.id, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'
FROM accounts a WHERE a.username = 'dealer36'
ON CONFLICT (id) DO NOTHING;

INSERT INTO schedules (id, customer, phone, address, date_time, status, account_id, created_at, updated_at)
SELECT 'sch-003', 'Pham Van K', '0911111111', '300 Cach Mang Thang 8, District 10, HCM', NOW() - INTERVAL '2 days', 'COMPLETED', a.id, NOW() - INTERVAL '5 days', NOW() - INTERVAL '2 days'
FROM accounts a WHERE a.username = 'dealer36'
ON CONFLICT (id) DO NOTHING;

-- Schedules cho dealer_hcm
INSERT INTO schedules (id, customer, phone, address, date_time, status, account_id, created_at, updated_at) VALUES
('sch-004', 'Hoang Thi L', '0922222222', '400 Vo Van Tan, District 3, HCM', NOW() + INTERVAL '1 day', 'CONFIRMED', 'acc-dealer-002', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
('sch-005', 'Nguyen Van M', '0933333333', '500 Tran Phu, Hoan Kiem, Hanoi', NOW() + INTERVAL '5 days', 'PENDING', 'acc-dealer-002', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Schedules cho dealer_hanoi
INSERT INTO schedules (id, customer, phone, address, date_time, status, account_id, created_at, updated_at) VALUES
('sch-006', 'Tran Thi N', '0944444444', '600 Hang Bai, Hoan Kiem, Hanoi', NOW() + INTERVAL '7 days', 'PENDING', 'acc-dealer-003', NOW(), NOW()),
('sch-007', 'Le Van O', '0955555555', '700 Tran Hung Dao, Hoan Kiem, Hanoi', NOW() - INTERVAL '3 days', 'CANCELLED', 'acc-dealer-003', NOW() - INTERVAL '4 days', NOW() - INTERVAL '3 days')
ON CONFLICT (id) DO NOTHING;

-- =============================================
-- VERIFICATION QUERIES
-- =============================================
-- SELECT COUNT(*) FROM categories; -- Expected: 4
-- SELECT COUNT(*) FROM accounts WHERE role = 'DEALER'; -- Expected: 3
-- SELECT COUNT(*) FROM vehicles; -- Expected: 5
-- SELECT COUNT(*) FROM inventories; -- Expected: 3
-- SELECT COUNT(*) FROM vehicle_inventory; -- Expected: 9
-- SELECT COUNT(*) FROM orders; -- Expected: 8
-- SELECT COUNT(*) FROM schedules; -- Expected: 7

-- Check dealer36's data:
-- SELECT * FROM orders WHERE account_id = 'acc-dealer-001'; -- Expected: 4 orders
-- SELECT * FROM schedules WHERE account_id = 'acc-dealer-001'; -- Expected: 3 schedules
-- SELECT * FROM inventories WHERE account_id = 'acc-dealer-001'; -- Expected: 1 inventory

-- =============================================
-- LOGIN CREDENTIALS FOR TESTING
-- =============================================
-- Admin:
--   username: admin123
--   password: password123 (or admin123 if DataInitializer creates it)

-- Dealers:
--   username: dealer36
--   password: password123 (or dealer123 if DataInitializer creates it)
--
--   username: dealer_hcm
--   password: password123
--
--   username: dealer_hanoi
--   password: password123

-- =============================================
-- IMPORTANT NOTES
-- =============================================
-- 1. Chạy script này SAU KHI BE đã start lần đầu (để DataInitializer tạo tables)
-- 2. Nếu dealer36 đã tồn tại từ DataInitializer, script sẽ update id và email
-- 3. Status chỉ dùng: PENDING, CONFIRMED, COMPLETED, CANCELLED
-- 4. CustomerInfo được map vào các field: customer, phone, address
