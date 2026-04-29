-- ============================================================
--  NeighborLink - Sample Data (data.sql)
-- ============================================================
USE neighborlink_db;

-- Societies
INSERT INTO SOCIETY (soc_name, city, address) VALUES
('SRM Heights',       'Chennai',   'Potheri'),
('Green Valley',      'Chennai',   'Tambaram'),
('Skyline Residency', 'Bangalore', 'Whitefield'),
('Urban Nest',        'Hyderabad', 'Gachibowli');

-- Users (password = "password123" plaintext for demo; hash in prod)
INSERT INTO USERS (soc_id, username, email, flat_no, trust_score, password) VALUES
(1, 'Viraj_D',        'viraj@mail.com',    'A-101', 4.9, 'password123'),
(1, 'Divye_B',        'divye@mail.com',    'A-102', 4.8, 'password123'),
(2, 'Rahul_K',        'rahul@mail.com',    'B-201', 4.5, 'password123'),
(2, 'Sneha_P',        'sneha@mail.com',    'B-202', 4.7, 'password123'),
(3, 'Aman_S',         'aman@mail.com',     'C-301', 4.6, 'password123'),
(4, 'Neha_R',         'neha@mail.com',     'D-401', 5.0, 'password123'),
(1, 'Aarav_Sharma',   'aarav@mail.com',    'A-103', 4.5, 'password123'),
(1, 'Rohan_Mehta',    'rohan@mail.com',    'A-104', 4.7, 'password123'),
(1, 'Priya_Nair',     'priya@mail.com',    'A-105', 4.9, 'password123'),
(2, 'Kunal_Verma',    'kunal@mail.com',    'B-203', 4.2, 'password123'),
(2, 'Sneha_Iyer',     'sneha2@mail.com',   'B-204', 4.8, 'password123'),
(2, 'Rahul_Kapoor',   'rahul2@mail.com',   'B-205', 4.6, 'password123');

-- Products
INSERT INTO PRODUCTS (owner_id, title, description, price_per_day, item_value) VALUES
(1, 'Camera',          'DSLR Camera with lens',       500.00,  20000.00),
(2, 'Drill Machine',   'Electric drill',              200.00,   5000.00),
(3, 'Tent',            'Camping tent 4 person',       300.00,   8000.00),
(4, 'Laptop',          'Gaming laptop',              1000.00,  70000.00),
(5, 'Guitar Lessons',  '1 hour session',              400.00,      0.00),
(6, 'Pressure Washer', 'High power washer',           350.00,  12000.00),
(1, 'DSLR Camera',     'Canon camera with lens',      800.00,  35000.00),
(2, 'Electric Drill',  'Bosch drill machine',         250.00,   6000.00),
(3, 'Camping Tent',    '4 person tent',               400.00,   9000.00),
(4, 'Gaming Laptop',   'High-end laptop',            1200.00,  80000.00),
(5, 'Guitar Classes',  'Beginner lessons',            500.00,      0.00),
(6, 'Pressure Washer', 'High power washer',           300.00,  15000.00),
(7, 'Projector',       'HD projector',                700.00,  20000.00),
(8, 'Cycle',           'Mountain bike',               200.00,   7000.00),
(9, 'Car Wash Kit',    'Complete cleaning kit',       150.00,   3000.00),
(10,'Cooking Service', 'Home chef for events',       1000.00,      0.00),
(11,'Sound System',    'Party speakers',              600.00,  25000.00),
(12,'Power Generator', 'Backup generator',            900.00,  50000.00),
(13,'Photography Svc', 'Event photography',          1500.00,      0.00),
(14,'Laptop Repair',   'Basic repair service',        400.00,      0.00),
(15,'Fitness Trainer', 'Personal training session',   800.00,      0.00);

-- Rentals
INSERT INTO RENTALS (product_id, renter_id, start_date, end_date, status) VALUES
(1,  2, '2026-02-10', '2026-02-12', 'COMPLETED'),
(2,  1, '2026-02-15', '2026-02-17', 'COMPLETED'),
(3,  4, '2026-03-01', '2026-03-03', 'ACTIVE'),
(4,  5, '2026-03-05', '2026-03-08', 'PENDING'),
(5,  6, '2026-03-10', '2026-03-11', 'COMPLETED'),
(1,  5, '2026-02-01', '2026-02-03', 'COMPLETED'),
(2,  3, '2026-02-05', '2026-02-07', 'COMPLETED'),
(3,  6, '2026-02-10', '2026-02-12', 'ACTIVE'),
(4,  7, '2026-02-15', '2026-02-18', 'PENDING'),
(5,  8, '2026-02-20', '2026-02-21', 'COMPLETED'),
(6,  9, '2026-02-22', '2026-02-25', 'COMPLETED'),
(7, 10, '2026-03-01', '2026-03-03', 'ACTIVE'),
(8, 11, '2026-03-04', '2026-03-06', 'PENDING'),
(9, 12, '2026-03-07', '2026-03-09', 'COMPLETED'),
(10,1, '2026-03-10', '2026-03-12', 'COMPLETED'),
(11,2, '2026-03-13', '2026-03-15', 'ACTIVE'),
(12,3, '2026-03-16', '2026-03-18', 'COMPLETED'),
(13,4, '2026-03-19', '2026-03-21', 'PENDING'),
(14,5, '2026-03-22', '2026-03-23', 'COMPLETED'),
(15,6, '2026-03-24', '2026-03-26', 'ACTIVE');

-- Insurance
INSERT INTO INSURANCE (rental_id, premium_amt, coverage_limit) VALUES
(1, 50.00, 20000.00),
(2, 30.00,  5000.00),
(3, 40.00,  8000.00),
(5, 50.00, 20000.00),
(6, 50.00, 20000.00);

-- Reviews
INSERT INTO REVIEWS (rental_id, reviewer_id, rating, comment) VALUES
(1, 2, 5, 'Excellent product, very helpful!'),
(2, 1, 4, 'Worked well, good condition'),
(5, 6, 5, 'Great experience'),
(6, 5, 5, 'Excellent'),
(7, 3, 4, 'Good');
